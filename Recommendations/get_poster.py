import requests
import os

def poster_function() :

    counter= 0

    """https://www.omdbapi.com/"""

    omdbapi_API_KEY = "???"
    PATH = "C:/suggestion_stocker/"

    def get_movie_poster(movie_name, year_of_release):

        url = f"http://www.omdbapi.com/?apikey={omdbapi_API_KEY}&t='{movie_name}'&y={year_of_release}"
        print(url)
        response = requests.get(url)
        print(response)

        if response.status_code == 200:

            data = response.json()
            print(data)
            if data == {'Response': 'False', 'Error': 'Movie not found!'} :
                url = f"http://www.omdbapi.com/?apikey={omdbapi_API_KEY}&t='{movie_name}'"
                response = requests.get(url)
                data = response.json()
                print(data)
                poster_path = data.get("Poster")

                return poster_path
            else :
                return data.get("Poster")
        else:

            print("Error:", response.status_code)
            return None
    """You can extract Runtime,resume and Language if you want // language matters"""

    for file in os.listdir(PATH) :

        if 'title' in file :

            counter += 1
            full_path = PATH+file
            f = open(full_path , 'r')
            title = f.read().strip()
            print(title)
            full_path_year = PATH + "release_date" + file[-1]
            try :
                f2 = open(full_path_year, 'r')
                year = f2.read().strip()[:4]
            except :
                year = 1996
                pass

            poster_path = get_movie_poster(title, year)



            if poster_path:
                print("Poster URL:", poster_path)
                save_path = 'C:/suggestion_stocker/'
                response = requests.get(poster_path)

                if response.status_code == 200:

                    # filename = os.path.basename(poster_path)
                    filename = 'Poster_IMG_'+str(counter)+'.png'

                    file_path = os.path.join(save_path, filename)

                    with open(file_path, "wb") as file:
                        file.write(response.content)

                    print(f"Image downloaded successfully to: {file_path}")
                else:
                    print("Failed to download the image.")

                poster_file = open('C:/suggestion_stocker/poster' + str(counter) + '.txt', 'w')
                poster_file.write(poster_path)
                poster_file.close()

            else:
                print("No poster found for the given title.")
