import oracledb

# 데이터베이스 연결 설정 함수
def get_db_connection():
    db_config = {
        'user': 'team11',  # 데이터베이스 사용자 이름
        'password': 'team11',  # 데이터베이스 비밀번호
        'dsn': '43.203.140.86:1521/xe'  # 데이터베이스 서비스 이름 (DSN)
    }
    return oracledb.connect(**db_config)  # 데이터베이스 연결 객체 반환

# 데이터 조회 함수
def load_green_db(userId): 
    connection = get_db_connection()  # 데이터베이스 연결 설정
    try:
        with connection.cursor() as cursor:
            # TBL_USERTOBOYUG 테이블에서 모든 데이터 조회
            cursor.execute("SELECT * FROM TBL_USERTOBOYUG")
            user_reservation_data = cursor.fetchall()

            # TBL_BOYUGTOUSER 테이블에서 모든 데이터 조회
            cursor.execute("SELECT * FROM TBL_BOYUGTOUSER")
            boyug_reservation_data = cursor.fetchall()
            
            # 특정 사용자 ID에 대한 사용자 정보 조회
            cursor.execute("""
                SELECT u.*, ud.*, uf.*
                FROM TBL_USER u
                JOIN TBL_USERDETAIL ud ON u.USERID = ud.USERID
                LEFT JOIN TBL_USERFAVORITE uf ON u.USERID = uf.USERID
                WHERE u.USERID = :userId 
            """, userId=userId)
            user_data = cursor.fetchall()

            # BOYUGUSERCONFIRM이 1인 BOYUG 사용자 데이터 조회
            cursor.execute("SELECT * FROM TBL_BOYUGUSER WHERE BOYUGUSERCONFIRM = 1")
            boyug_user_data = cursor.fetchall()

            # BOYUGPROGRAMACTIVE가 1인 BOYUG 프로그램 데이터 조회
            cursor.execute("SELECT * FROM TBL_BOYUGPROGRAM WHERE BOYUGPROGRAMACTIVE = 1")
            program_data = cursor.fetchall()

            # TBL_SESSION 테이블에서 모든 데이터 조회
            cursor.execute("SELECT * FROM TBL_SESSION")
            program_session_data = cursor.fetchall()

        # 조회된 데이터 반환
        return user_reservation_data, boyug_reservation_data, user_data, program_data, program_session_data, boyug_user_data
    finally:
        connection.close()  # 데이터베이스 연결 닫기

# 채팅 기록 조회 함수
def load_chat_history_green_db(gptTalkId, userId):
    connection = get_db_connection()  # 데이터베이스 연결 설정
    try:
        with connection.cursor() as cursor:
            cursor.execute("""
                SELECT d.*, t.* 
                FROM TBL_GPTTALKDETAIL d 
                JOIN TBL_GPTTALK t ON d.GPTTALKID = t.GPTTALKID 
                WHERE t.USERID = :userId 
                AND d.GPTTALKID = :gptTalkId 
                ORDER BY d.GPTDETAILID ASC
            """, userId=userId, gptTalkId=gptTalkId)
            columns = [col[0] for col in cursor.description]
            chat_list = cursor.fetchall()
            
            # 튜플을 딕셔너리로 변환하고 LOB 데이터를 문자열로 변환
            converted_chat_list = []
            for row in chat_list:
                converted_row = {columns[i]: (value.read() if hasattr(value, 'read') else value) for i, value in enumerate(row)}
                converted_chat_list.append(converted_row)
                
        return converted_chat_list
    finally:
        connection.close()  # 데이터베이스 연결 닫기

# 고유한 채팅 조회 함수
def load_unique_chat_green_db(userId):
    connection = get_db_connection()  # 데이터베이스 연결 설정
    try:
        with connection.cursor() as cursor:
            cursor.execute("""
                SELECT 
                    d.GPTDETAILID, 
                    d.GPTREQUEST, 
                    t.GPTTALKID 
                FROM 
                    TBL_GPTTALKDETAIL d 
                JOIN 
                    TBL_GPTTALK t 
                ON 
                    d.GPTTALKID = t.GPTTALKID 
                WHERE 
                    t.USERID = :userId 
                    AND t.GPTTALKACTIVE = 1 
                    AND d.GPTDETAILID = (
                        SELECT MAX(d2.GPTDETAILID)
                        FROM TBL_GPTTALKDETAIL d2
                        WHERE d2.GPTTALKID = d.GPTTALKID
                    )
                ORDER BY 
                    d.GPTDETAILID DESC
            """, userId=userId)
            columns = [col[0] for col in cursor.description]
            chat_list = cursor.fetchall()
            
            # 튜플을 딕셔너리로 변환하고 LOB 데이터를 문자열로 변환
            converted_chat_list = []
            for row in chat_list:
                converted_row = {columns[i]: (value.read() if hasattr(value, 'read') else value) for i, value in enumerate(row)}
                converted_chat_list.append(converted_row)
                
        return converted_chat_list
    finally:
        connection.close()  # 데이터베이스 연결 닫기

# 채팅 상태 업데이트 함수
def update_chat_list_green_db(gptTalkId):
    connection = get_db_connection()  # 데이터베이스 연결 설정
    try:
        with connection.cursor() as cursor:
            # 특정 GPTTALKID의 GPTTALKACTIVE 값을 0으로 업데이트
            cursor.execute("UPDATE TBL_GPTTALK SET GPTTALKACTIVE = 0 WHERE GPTTALKID = :gptTalkId", gptTalkId=gptTalkId)
            connection.commit()  # 변경 사항 커밋

    except Exception as e:
        connection.rollback()  # 예외 발생 시 롤백
        print(e)  # 예외 메시지 출력
    finally:
        connection.close()  # 데이터베이스 연결 닫기

# 새로운 채팅 삽입 함수
def insert_chat_green_db(chatCurrentRoomId, userId):
    connection = get_db_connection()  # 데이터베이스 연결 설정
    try:
        with connection.cursor() as cursor:
            # 새로운 채팅 방을 TBL_GPTTALK 테이블에 삽입
            cursor.execute("INSERT INTO TEAM11.TBL_GPTTALK (GPTTALKID, USERID) VALUES (:chatCurrentRoomId, :userId)",
                           {'chatCurrentRoomId': chatCurrentRoomId, 'userId': userId})
            connection.commit()  # 변경 사항 커밋

    except Exception as e:
        connection.rollback()  # 예외 발생 시 롤백
        print(f"Error inserting chat into database: {e}")  # 예외 메시지 출력
    finally:
        connection.close()  # 데이터베이스 연결 닫기

# 채팅 상세 정보 삽입 함수
def insert_chat_detail_green_db(chatCurrentRoomId, responseData, userMessage, userId):
    connection = get_db_connection()  # 데이터베이스 연결 설정
    try:
        with connection.cursor() as cursor:
            # 새로운 채팅 상세 정보를 TBL_GPTTALKDETAIL 테이블에 삽입
            cursor.execute("""
                INSERT INTO TEAM11.TBL_GPTTALKDETAIL (GPTTALKID, GPTRESPONSE, GPTREQUEST, USERID) 
                VALUES (:chatCurrentRoomId, :responseData, :userMessage, :userId)
            """, {
                'chatCurrentRoomId': chatCurrentRoomId, 
                'responseData': responseData, 
                'userMessage': userMessage,
                'userId': userId
            })
            connection.commit()  # 변경 사항 커밋

    except Exception as e:
        connection.rollback()  # 예외 발생 시 롤백
        print(f"Error inserting chat detail into database: {e}")  # 예외 메시지 출력
    finally:
        connection.close()  # 데이터베이스 연결 닫기