# 기존 Dockerfile의 내용
FROM python:3.9-slim

# 작업 디렉토리 설정
WORKDIR /app

# 필요한 패키지 설치
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 추가: python-multipart 패키지 설치
RUN pip install python-multipart

# 애플리케이션 코드 복사
COPY . .

# 애플리케이션 실행
CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "9999"]