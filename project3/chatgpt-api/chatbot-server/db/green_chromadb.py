import openai
import chromadb
from chromadb.config import Settings, DEFAULT_DATABASE, DEFAULT_TENANT

# ChromaDB 클라이언트 설정
# PersistentClient를 사용하여 벡터 데이터베이스에 연결합니다.
chromadb_client = chromadb.PersistentClient(
    path="vectordb",  # 벡터 데이터베이스의 경로를 지정합니다.
    settings=Settings(),  # 기본 설정을 사용합니다.
    tenant=DEFAULT_TENANT,  # 기본 테넌트를 사용합니다.
    database=DEFAULT_DATABASE,  # 기본 데이터베이스를 사용합니다.
)

# 컬렉션 이름을 정의합니다.
collection_name = "recruit_documents"

# recruit_documents 컬렉션을 가져옵니다.
# 컬렉션이 존재하지 않으면 오류가 발생합니다.

# recruit_collection = chromadb_client.get_or_create_collection(name=collection_name)
# recruit_collection = chromadb_client.create_collection(name="recruit_documents")
try:
    recruit_collection = chromadb_client.get_collection(name=collection_name)
except chromadb.errors.InvalidCollectionException:
    recruit_collection = chromadb_client.create_collection(name=collection_name)
# recruit_collection = None


def generate_embedding(text):
    # OpenAI API를 사용하여 텍스트 임베딩을 생성합니다.
    response = openai.embeddings.create(
        input=text,  # 임베딩을 생성할 텍스트를 입력합니다.
        model="text-embedding-ada-002"  # 사용할 모델을 지정합니다.
    )
    # 응답에서 임베딩 벡터를 추출하여 반환합니다.
    return response.data[0].embedding

def store_document(text):
    # 텍스트의 임베딩을 생성합니다.
    embedding = generate_embedding(text)

    # 텍스트와 임베딩을 벡터 데이터베이스에 추가합니다.
    recruit_collection.add(
        documents=[text],  # 문서 리스트에 텍스트를 추가합니다.
        embeddings=[embedding],  # 임베딩 리스트에 임베딩을 추가합니다.
        ids=[text[:50]]  # 문서 ID로 텍스트의 처음 50자를 사용합니다.
    )

def query_similar_documents(query, top_k=5):
    # 쿼리의 임베딩을 생성합니다.
    query_embedding = generate_embedding(query)

    # 유사도를 기반으로 관련성이 높은 문서를 조회합니다.
    results = recruit_collection.query(
        query_embeddings=[query_embedding],  # 쿼리 임베딩을 입력합니다.
        n_results=top_k  # 반환할 유사 문서의 수를 지정합니다.
    )

    # 조회 결과를 반환합니다.
    return results

def delete_collection():
    # 컬렉션을 삭제합니다.
    chromadb_client.delete_collection(collection_name)
