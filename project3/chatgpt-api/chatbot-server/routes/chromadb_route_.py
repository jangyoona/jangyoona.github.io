from fastapi import APIRouter

import os

from db import green_chromadb, green_chroma_dao  # 데이터베이스 관련 모듈 임포트

chromadb_router = APIRouter()  # FastAPI 라우터 생성

@chromadb_router.get("/init-embedding-db")
async def init_embedding_db():
    # 모든 채용 정보 로드
    recruits = green_chroma_dao.load_all_green()

    # 컬럼 정의
    columns = [
        "u.BOYUGPROGRAMDETAILID AS USER_BOYUGPROGRAMDETAILID",
        "u.BOYUGSCORE AS USER_BOYUGSCORE",
        "u.USERID AS USER_USERID",
        "u.USERSCORE AS USER_USERSCORE",
        "u.REQUESTDATE AS USER_REQUESTDATE",
        "u.REQUESTISOK AS USER_REQUESTISOK",
        "b.BOYUGPROGRAMDETAILID AS BOYUG_BOYUGPROGRAMDETAILID",
        "b.BOYUGSCORE AS BOYUG_BOYUGSCORE",
        "b.BOYUGTOUSERID AS BOYUG_BOYUGTOUSERID",
        "b.USERSCORE AS BOYUG_USERSCORE",
        "b.USERSESSIONID AS BOYUG_USERSESSIONID",
        "b.REQUESTDATE AS BOYUG_REQUESTDATE",
        "b.REQUESTISOK AS BOYUG_REQUESTISOK",
        "p.BOYUGPROGRAMACTIVE",
        "p.BOYUGPROGRAMCOUNT",
        "p.BOYUGPROGRAMID",
        "p.BOYUGPROGRAMISOPEN",
        "p.USERID AS PROGRAM_USERID", 
        "p.BOYUGPROGRAMMODIFYDATE",
        "p.BOYUGPROGRAMREGDATE",
        "p.BOYUGPROGRAMNAME"
    ]

    # 각 채용 정보를 벡터 데이터베이스에 저장
    for idx, row in enumerate(recruits):
        doc = ("[{0}:{1}]".format(c, v) for c, v in zip(columns, row))  # 벡터 데이터베이스에 저장될 텍스트 생성
        green_chromadb.store_document("  ".join(doc))  # 텍스트 저장
        
        print("--------------> {0} data is saved".format(idx + 1))  # 저장된 데이터 개수 출력

    return {"metadata": "all recruits", "data": recruits[:5]}  # 메타데이터와 일부 데이터 반환