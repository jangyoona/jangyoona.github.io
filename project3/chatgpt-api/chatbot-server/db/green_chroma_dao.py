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
def load_all_green(): 
    connection = get_db_connection()  # 데이터베이스 연결 설정
    try:
        with connection.cursor() as cursor:
            # 세 개의 테이블을 조인하여 데이터 조회
            cursor.execute("""
                SELECT 
                    u.BOYUGPROGRAMDETAILID AS USER_BOYUGPROGRAMDETAILID,
                    u.BOYUGSCORE AS USER_BOYUGSCORE,
                    u.USERID AS USER_USERID,
                    u.USERSCORE AS USER_USERSCORE,
                    u.REQUESTDATE AS USER_REQUESTDATE,
                    u.REQUESTISOK AS USER_REQUESTISOK,
                    b.BOYUGPROGRAMDETAILID AS BOYUG_BOYUGPROGRAMDETAILID,
                    b.BOYUGSCORE AS BOYUG_BOYUGSCORE,
                    b.BOYUGTOUSERID AS BOYUG_BOYUGTOUSERID,
                    b.USERSCORE AS BOYUG_USERSCORE,
                    b.USERSESSIONID AS BOYUG_USERSESSIONID,
                    b.REQUESTDATE AS BOYUG_REQUESTDATE,
                    b.REQUESTISOK AS BOYUG_REQUESTISOK,
                    p.BOYUGPROGRAMACTIVE,
                    p.BOYUGPROGRAMCOUNT,
                    p.BOYUGPROGRAMID,
                    p.BOYUGPROGRAMISOPEN,
                    p.USERID AS PROGRAM_USERID,
                    p.BOYUGPROGRAMMODIFYDATE,
                    p.BOYUGPROGRAMREGDATE,
                    p.BOYUGPROGRAMNAME
                FROM TBL_USERTOBOYUG u
                JOIN TBL_BOYUGTOUSER b ON u.BOYUGPROGRAMDETAILID = b.BOYUGPROGRAMDETAILID
                JOIN TBL_BOYUGPROGRAM p ON u.BOYUGPROGRAMDETAILID = p.BOYUGPROGRAMID
                WHERE p.BOYUGPROGRAMACTIVE = 1
            """)
            combined_data = cursor.fetchall()

        # 조회된 데이터 반환
        return combined_data
    finally:
        connection.close()  # 데이터베이스 연결 닫기