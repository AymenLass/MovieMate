import requests

omdbapi_API_KEY = "2f87f01f"


def get_movie_poster(movie_name, year_of_release):
    url = f"http://www.omdbapi.com/?apikey={omdbapi_API_KEY}&t={movie_name}&y={year_of_release}"

    response = requests.get(url)

    if response.status_code == 200:

        data = response.json()

        poster_path = data.get("Poster")

        return poster_path
    else:

        print("Error:", response.status_code)
        return None


response = "title :   My Father the Hero   /  Why did it choose it  :  "
title = response.split("/")[0].split("title :")[1].strip()
poster_path = get_movie_poster(title, 1991)
if poster_path:
    print("Poster URL:", poster_path)
else:
    print("No poster found for the given title.")