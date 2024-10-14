from fastapi import APIRouter, UploadFile, File
from openai import OpenAI
import os
import shutil
from models.chatbot_models import ChatConversation, ChatDetail
from db import green_dao
from db import green_chromadb

# FastAPI 라우터 설정
chatbot_router = APIRouter()

# OpenAI API 키 설정
os.environ["OPENAI_API_KEY"] = ""
# OpenAI 클라이언트 초기화
client = OpenAI()

# 텍스트 채팅 처리 엔드포인트
@chatbot_router.post("/chat-text")
async def process_chat_text(msg: ChatConversation, userId: int):
    userId = userId
   
    # 데이터베이스에서 사용자 관련 데이터 로드
    user_reservation_data, boyug_reservation_data, user_data, program_data, program_session_data, boyug_user_data = green_dao.load_green_db(userId)
    
    # 데이터 컬럼 이름 정의
    user_columns = ["자동증가 값", "카테고리", "ID", "가입날자", "우편번호", "주소", "상세주소", "주소 위도", "주소 경도", "이름", "전화번호", "비밀번호", "권한", "소셜ID", "ID", "생년월일", "성별", "간략한 건강상태", "전화번호", "선호하는 활동 번호", "ID"]
    boyug_user_columns = ["보육원 아이들의 수", "사이트 접근 승인 여부", "사용자 ID", "보육원 email", "보육원 이름"]
    user_reservation_columns = ["보육 프로그램ID", "유저가 받은 점수", "유저ID", "보육원이 받은 점수", "신청 날자", "현재상태"]
    boyug_reservation_columns = ["보육 프로그램ID", "보육원이 받은 점수", "자동증가 값", "유저가 받은 점수", "유저ID", "신청 날자", "현재상태"]
    program_columns = ["글 삭제 여부", "조회수", "보육 프로그램ID", "보육 프로그램 활성 여부", "사용자ID", "글 수정 날자", "글 작성 날자", "공고 내용", "모집 공고 특이사항"]
    program_session_columns = ["자동증가값", "활동번호", "활동 이름"]

    # 제외할 컬럼의 인덱스 정의
    user_exclude_indices = [0, 1, 12, 14, 20]
    boyug_user_exclude_indices = [1]
    user_reservation_exclude_indices = [0]
    boyug_reservation_exclude_indices = [0, 2]
    program_exclude_indices = [0, 2, 3]
    program_session_exclude_indices = [0]

    # 변환된 데이터 리스트 생성
    filtered_user_data = []
    for row in user_data:
        filtered_user_data.append({user_columns[i]: row[i] for i in range(len(user_columns)) if i not in user_exclude_indices})

    filtered_boyug_user_data = []
    for row in boyug_user_data:
        filtered_boyug_user_data.append({boyug_user_columns[i]: row[i] for i in range(len(boyug_user_columns)) if i not in boyug_user_exclude_indices})
    
    filtered_user_reservation_data = []
    for row in user_reservation_data:
        filtered_user_reservation_data.append({user_reservation_columns[i]: row[i] for i in range(len(user_reservation_columns)) if i not in user_reservation_exclude_indices})

    filtered_boyug_reservation_data = []
    for row in boyug_reservation_data:
        filtered_boyug_reservation_data.append({boyug_reservation_columns[i]: row[i] for i in range(len(boyug_reservation_columns)) if i not in boyug_reservation_exclude_indices})

    filtered_program_data = []
    for row in program_data:
        filtered_program_data.append({program_columns[i]: row[i] for i in range(len(program_columns)) if i not in program_exclude_indices})
    
    filtered_program_session_data = []
    for row in program_session_data:
        filtered_program_session_data.append({program_session_columns[i]: row[i] for i in range(len(program_session_columns)) if i not in program_session_exclude_indices})

    # 여러 결과를 프롬프트에 포함시키기 위해 데이터를 문자열로 변환
    user_reservation_info = "\n".join([str(row) for row in filtered_user_reservation_data])
    boyug_reservation_info = "\n".join([str(row) for row in filtered_boyug_reservation_data])
    user_info = "\n".join([str(row) for row in filtered_user_data])
    boyug_user_info = "\n".join([str(row) for row in filtered_boyug_user_data])
    program_info = "\n".join([str(row) for row in filtered_program_data])
    program_session_info = "\n".join([str(row) for row in filtered_program_session_data])

    query = msg.conversation[-1] # 마지막 질문 요소 반환
    selected_documents = green_chromadb.query_similar_documents(query, top_k=10) # vectordb에서 질문과 관련된 데이터 조회
    
    # 유사도 점수가 0.8 이상인 문서만 선택
    selected_documents2 = [
    [doc, dist] for (doc, dist) in zip(selected_documents['documents'][0], selected_documents['distances'][0]) if dist >= 0.8
]

    if not selected_documents2:
        context_str = "유사한 답변을 찾을 수 없습니다."
        
    else:
        context_str = '\n'.join(["recruid-{0}. {1}".format(idx + 1, doc) for idx, (doc, dist) in enumerate(selected_documents2)])
        
    
    # OpenAI API 호출을 위한 프롬프트 설정
    prompt = f"""
    역할: 노인 자원봉사자와 보육원을 연결하는 웹사이트의 한국어 기반 챗봇
    목표1: 노인이 보육원에서의 봉사활동 정보를 쉽게 찾을 수 있도록 지원합니다.
    목표2: 보육원이 필요한 자원봉사자를 효율적으로 구인할 수 있도록 돕습니다.
    목표3: 사용자가 자원봉사 활동, 특히 아이에게 책 읽어주기 봉사에 대한 정보와 절차를 쉽게 이해할 수 있도록 안내합니다.
    기능:
      - 봉사활동 안내: 봉사활동 종류 및 신청 방법 설명
      - 자원봉사자 매칭: 사용자의 관심사와 위치에 맞는 보육원 추천 백터 데이터베이스에서 유사한 문서(없을 수 있음)가 있다면 우선적으로 사용합니다.
      보육 프로그램 추천할때 항상 http://192.168.0.15:8081/userView/activityPages/boyugProgramDetail?boyugProgramId=10952 이런식으로 링크를 제공해주시면 좋을 것 같습니다. boyugProgramId=보육프로그램ID 
      - 일정 관리: 봉사 일정 확인 및 예약 기능 제공
      - FAQ 지원: 자주 묻는 질문에 대한 신속한 답변

    ----------------------------------------------

    유저가 보육원에게 신청한 예약 전체내역:
    {user_reservation_info}

    보육원이 유저에게 신청한 예약 전체내역:
    {boyug_reservation_info}

    사용자 정보:
    {user_info}

    보육원 정보:
    {boyug_user_info}

    현재 모집중인 자원봉사 모집 글 정보:
    {program_info}

    사이트에 등록된 자원봉사 종류:
    {program_session_info}

    백터 데이터베이스에서 유사한 문서(없을 수 있음):
    {context_str}
    """
    
    messages = [
        {"role": "system", "content": prompt}
    ]
    
    # msg.conversation 배열의 마지막 20개 요소를 messages 리스트에 추가
    conversation_length = len(msg.conversation)
    start_index = max(0, conversation_length - 20)
    for chat_message in msg.conversation[start_index:]:
        messages.append({'role': chat_message.role, 'content': chat_message.content})
        
    # OpenAI API 호출
    response = client.chat.completions.create(
        model="gpt-4o-mini",
        messages=messages,
        n=1, 
        temperature=1,
        max_tokens=300
    )
    
    # 응답 메시지 추출
    response_message = response.choices[0].message.content
    
    return {
        "metadata": "chatbot response",
        "message": response_message
    }

# 채팅 목록 조회 엔드포인트
@chatbot_router.get("/chat-list")
async def process_chat_list(userId: int):
    chat_list = green_dao.load_unique_chat_green_db(userId)
    return chat_list

# 채팅 목록 삭제 엔드포인트
@chatbot_router.get("/chat-list-delete")
async def process_chat_list(gptTalkId: int):
    chat_list = green_dao.update_chat_list_green_db(gptTalkId)
    return {"status": "success"}

# 채팅 기록 조회 엔드포인트
@chatbot_router.get("/chat-history")
async def get_chat_history(gptTalkId: int, userId: int):
    chat_list = green_dao.load_chat_history_green_db(gptTalkId, userId)
    return chat_list

# 채팅 기록 저장 엔드포인트
@chatbot_router.post("/chat-history-save")
async def save_chat(chatCurrentRoomId: int, userId: int):
    green_dao.insert_chat_green_db(chatCurrentRoomId, userId)
    return {"status": "success"}

# 채팅 상세 기록 저장 엔드포인트
@chatbot_router.post("/chat-detail-history-save")
async def save_chat_detail(chat_detail: ChatDetail):
    green_dao.insert_chat_detail_green_db(chat_detail.chatCurrentRoomId, chat_detail.responseData, chat_detail.userMessage, chat_detail.userId)
    return {"status": "success"}

# 오디오 파일을 텍스트로 변환하는 엔드포인트
@chatbot_router.post("/audio-to-text")
async def process_audio(audio: UploadFile = File(...)) -> str:
    print("Received audio file: ", audio.filename)

    # 오디오 파일을 로컬에 저장
    with open(audio.filename, "wb") as f:
        shutil.copyfileobj(audio.file, f)

    # 저장된 오디오 파일을 읽어와서 OpenAI API로 전송
    with open(audio.filename, "rb") as f:
        transcription = client.audio.transcriptions.create(
            model='whisper-1',
            file=f
        )

    return transcription.text


