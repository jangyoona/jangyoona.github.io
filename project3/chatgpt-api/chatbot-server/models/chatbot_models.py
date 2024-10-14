from typing import List
from pydantic import BaseModel

class ChatMessage(BaseModel):
    role: str
    content: str

class ChatConversation(BaseModel):
    conversation: List[ChatMessage]


class ChatDetail(BaseModel):
    chatCurrentRoomId: int
    responseData: str
    userMessage: str
    userId: int