import uvicorn # tomcat
from fastapi import FastAPI # servlet
from fastapi.middleware.cors import CORSMiddleware # cors

# from routes.admin_route import admin_router # controller
from routes.chatbot_route import chatbot_router # controller
from routes.chromadb_route_ import chromadb_router # controller
app = FastAPI()

origins = [
    "http://localhost:8080",
    "http://127.0.0.1:8080",
    "http://192.168.0.15:8080",
    "http://localhost:5001",

    "http://localhost:8081",
    "http://127.0.0.1:8081",
    "http://192.168.0.15:8081",
    "http://192.168.0.15:9999"
]
app.add_middleware(
    CORSMiddleware,
    allow_origins = origins,
    allow_credentials=True,
    allow_methods=["POST", "GET"],
    allow_headers=["*"]
)

# app.include_router(admin_router, prefix="/admin")
app.include_router(chatbot_router, prefix="/chatbot")
app.include_router(chromadb_router, prefix="/chromadb")

if __name__ == "__main__": 
    uvicorn.run("app:app", host='0.0.0.0', port=9999) # localhost, 127.0.0.1, 192.168.0.14