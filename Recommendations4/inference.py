import random
from get_answers import get_query
from get_poster import poster_function
# from langchain.text_splitter import CharacterTextSplitter , RecursiveCharacterTextSplitter
from sentence_transformers import SentenceTransformer
# from content import cont_list
import os
from supabase import create_client
# When you create a file .env you should do that other then importing the filename as always
from dotenv import load_dotenv
load_dotenv()
from sklearn.metrics.pairwise import cosine_similarity
from lang_model import Agent
import json
import time


url= os.environ.get("supabase_url")
key = os.environ.get("supabase_key")
supabase = create_client(url, key)
print("Sucessufully connected to the database !")

# supabase = Client(supabase_url, supabase_key)

model = SentenceTransformer('intfloat/e5-large-v2')

# from sentence_transformers import SentenceTransformer
# from char_splitter import CharacterTextSplitter

# Initialize the SentenceTransformer model
# model = SentenceTransformer('distilbert-base-nli-mean-tokens')
def text_splitting():
    with open('movies_data.txt', 'r', encoding='utf-8') as file:
        content = file.read()


    paragraphs = content.strip().split('\n\n')

    return paragraphs


def embedding():
    paragraphs = text_splitting()

    for paragraph in paragraphs:

        embedding = model.encode(paragraph, normalize_embeddings=True)
        embedding_list = embedding.tolist()


        embedding_json = json.dumps(embedding_list)

        data = {
            "content": paragraph,
            "embedding": embedding_json
        }

        supabase.table("movies").insert(data).execute()

        time.sleep(1)

    print("Items have been successfully added to the database!")


# def text_splitting():
#     # file = open('movies_data.txt', "r")
#     with open('movies_data.txt', 'r', encoding='utf-8') as file:
#         content = file.read()
#     podcasts = content
#     # output = podcasts.split('\n\n')
#     #RecursiveCharacterTextSplitter
#     # splitter = CharacterTextSplitter( chunk_size=150, chunk_overlap=15 , separator= '\n\n')
#     splitter = RecursiveCharacterTextSplitter(chunk_size=500, chunk_overlap=35)
#     output = splitter.create_documents([podcasts])
#     return output
#
# def embedding():
#     doc = text_splitting()
#     cont_list = [document.page_content for document in doc]
#     print(cont_list)
#     for textchunk in cont_list:
#         print(textchunk)
#         embedding = model.encode(textchunk, normalize_embeddings=True)
#         embedding_list = embedding.tolist()
#
#         # Convert Python list to JSON string
#         embedding_json = json.dumps(embedding_list)
#         # the embedding should be json that is a condition for supabase if you give an ndarry it will result to an error
#         data = {
#             "content": textchunk,
#             "embedding": embedding_json
#         }
#         supabase.table("movies").insert(data).execute()
#         # Insert content and embedding into Supabase
#         time.sleep(2)
#
#     print("Items have Successfully been added to the database!")
def match_movies(query, threshold=0.75 , match_count =1):
    """
    Match the given query to movie names based on their embeddings.

    Args:
        query (str): The query string.
        threshold (float): Threshold for considering a match.

    Returns:
        list: A list of movie names that match the query.
    """

    query_embedding = model.encode(query, normalize_embeddings=True)


    response = supabase.table("movies").select("*").execute()


    movie_embeddings = [item["embedding"] for item in response.data]

    """https://supabase.com/docs/reference/python/insert"""

#    # print(len(movie_embeddings)) 156 row

    matched_movies = dict()


    movie_content = [item["content"] for item in response.data]
    counter = 0

    for movie_data in movie_embeddings:


        embedding_list = json.loads(movie_data)

        similarity = cosine_similarity([query_embedding], [embedding_list])[0][0]

        if similarity >= threshold:
            matched_movies[movie_content[counter]] = similarity
        counter+=1

    sorted_movies = dict(sorted(matched_movies.items(), key=lambda x: x[1], reverse=True))

    return list(sorted_movies.keys())[0 : match_count]

suggestions = dict()

counter = 1
def answers(text , query) :

    global counter
    PATH ='C:/suggestion_stocker/'


    agent = Agent()
    completion1 = agent.get_chat_completion(text, query)

    completion1_list = completion1.split("\n")

    for answers in completion1_list:
        if "title" in answers:
            title = answers
            title = title.split(':')[-1].strip()
        if "release_date" in answers:
            release_date = answers
            release_date = release_date.split(':')[-1].strip()
        if "overview" in answers:
            overview = answers
            overview = overview.split(':')[-1].strip()
        if "Answer" in answers:
            answer = answers
            answer = answer.split(':')[-1].strip()

    print("*" * 50)

    class colors:
        HEADER = '\033[95m'
        OKBLUE = '\033[94m'
        OKGREEN = '\033[92m'
        WARNING = '\033[93m'
        FAIL = '\033[91m'
        ENDC = '\033[0m'
        BOLD = '\033[1m'
        UNDERLINE = '\033[4m'


    print(colors.HEADER + "title test : " + colors.ENDC, title)
    print(colors.OKBLUE + "release_date test : " + colors.ENDC, release_date)
    print(colors.OKGREEN + "overview test : " + colors.ENDC, overview)
    print(colors.WARNING + "answer test : " + colors.ENDC, answer)
    # print("title test : ", title)
    # print("release_date test : ", release_date)
    # print("overview test : ", overview)
    # print("answer test : ", answer)

    print("*" * 50)

    elements = [(title,"title") ,(release_date,"release_date") , (overview,"overview"), (answer,"answer")]
    for element , name in elements :
        file = open(PATH+str(name)+str(counter) , "w")
        file.write(element)
        file.close()

    counter+=1

    # suggestions['title'] = title
    # suggestions['release_date'] = release_date
    # suggestions['overview'] = overview
    # suggestions['answer'] = answer

    return title , release_date , overview , answer
def main():
    # embedding()

    global counter
    query = get_query()
    number = 50
    threshold = 0.8
    text = []

    while len(text) < number:
        text = match_movies(query, threshold, number)

        if len(text) < number:
            threshold -= 0.05

    print(f"Threshold {threshold} yielded {len(text)} outputs.")

    i = 0
    j = 0
    while i == j:
        i = random.randint(0, number - 1)
        j = random.randint(0, number - 1)

    print(text)
    if text:
        text1 = text[i][:266] if len(text[i]) > 266 else text[i]

        # title_start_index = text1.find("title :") + len("title :")  # Find the starting index of title
        # title_end_index = text1.find("release_date")  # Find the ending index of title
        # title = text1[title_start_index:title_end_index].strip()
        #

        # overview_start_index = text1.find("overview :") + len("overview :")  # Find the starting index of overview
        # overview_end_index = text1.find("Question")  # Find the ending index of overview
        # overview = text1[overview_start_index:overview_end_index].strip()
        #

        # answer_start_index = text1.find("Answer:") + len("Answer:")  # Find the starting index of answer
        # answer = text1[answer_start_index:].strip()

        text2 = text[j][:266] if len(text[j]) > 266 else text[j]

        # print(text2)


        answers(text1 , query)
        answers(text2, query)
        counter = 1
        poster_function()


    else:
        print("Sorry, I don't know the answer.")

