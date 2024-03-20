import requests

API_TOKEN = "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"
API_URL = "https://api-inference.huggingface.co/models/mistralai/Mixtral-8x7B-Instruct-v0.1"
headers = {"Authorization": f"Bearer {API_TOKEN}"}

class Agent():
    def __init__(self):
        self.data = {
            "role": "system",
            "content": 'You are an enthusiastic movie expert who loves recommending movies to people.You will be given some information - some context about movies and a question.Your main job is to formulate a short answer to the question using the provided context.You should provdie evrey answer film names  the title , duration and description . If you are unsure and cannot find the answer in the context, say, "Sorry, I dont know the answer." Please do not make up the answer.' ,
            "parameters": {
                "max_new_tokens": 2000,
                "return_full_text": True,
            }
        }
        self.memory = {}

    def get_chat_completion(self, text, query):
        self.data["role"] = 'system'
        self.data["inputs"] = f'Context: {text}\nQuestion: {query}'

        if text in self.memory:
            self.data["inputs"] += f'\nMemory: {self.memory[text]}'

        response = requests.post(API_URL, headers=headers, json=self.data)
        result = response.json()
        generated_text = result[0]['generated_text']

        # Store generated response in memory
        self.memory[text] = generated_text

        return generated_text