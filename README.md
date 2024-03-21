
<p align = "center">
<img src="https://mega.nz/fm/qVhDDYaJ">
</p>



# MovieMate: Movie Recommendation App

MovieMate is an innovative movie recommendation application that leverages cutting-edge technologies to provide personalized movie suggestions tailored to the preferences of its users. By combining dynamic pagination, web scraping, machine learning, and natural language processing, MovieMate delivers a seamless and intuitive user experience.

## Features

- **Dynamic Pagination:** Movie recommendations are tailored based on the number of persons intending to watch the movie, ensuring personalized suggestions for every user scenario.

- **Web Scraping:** Utilizing web scraping techniques, MovieMate extracts relevant data related to movies from various sources, enriching its recommendation database with up-to-date information.

- **Pretrained Model Embedding:** Each movie in the database is embedded into a 1024-dimensional vector space using a pretrained model (e5-large-v2), enabling efficient similarity calculations for recommendation purposes.

- **Vector Database:** Movie data, represented as embedded vectors, is stored in a vector database (Supabase), facilitating quick and scalable access to movie information.

- **Flask Server:** A Flask server manages the flow of data between the mobile app and the inference engine, handling REST API requests and responses effectively.

- **Machine Learning Inference:** User inputs, including preferences and watch duration, are processed by Python functions to generate embedded vectors, which are then used to calculate cosine similarity scores for movie recommendations.

- **Natural Language Processing with Langchain:** Movie selections are further analyzed using Langchain, where queries are posed to generate insightful answers about the chosen movies, providing users with detailed explanations and insights.

- **Poster Extraction:** Movie posters are fetched using the OMDB API, enhancing the visual appeal of the app and providing users with additional context about the recommended movies.

## Usage

1. **Input Requirements:** Users are prompted to provide details such as the number of viewers, duration of viewing, and individual preferences for each viewer.

2. **Data Processing:** User inputs are processed and concatenated into embedded vectors using the pretrained model.

3. **Recommendation Generation:** Cosine similarity scores are calculated between user vectors and the vectors of movies in the database. The top-scoring movies are recommended to the user.

4. **Insightful Analysis:** Langchain is employed to analyze user selections, generating comprehensive insights into the chosen movies and the reasons behind the recommendations.

5. **Visual Presentation:** Recommended movies, along with their posters and insights, are displayed to the user via the Flask server, providing an engaging and informative experience.

## Technologies and Tools Used

- Android Studio
- Python
- Flask
- Web Scraping
- Pretrained Models (e5-large-v2)
- Supabase (Vector Database)
- Hugging Face API
- OMDB API
- Langchain

