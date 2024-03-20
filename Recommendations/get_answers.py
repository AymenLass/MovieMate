
import os
def get_query() :
    PATH = "C:/response_stocker/"
    queryy = ""
    with open(PATH+"number_time.txt" , "r") as f :
        for line in f:
            line = line.strip()
            queryy += line

    for file in os.listdir(PATH) :
        if file!="number_time.txt" :
            with open(PATH+file , "r") as f :
                # file_content = f.read()
                for line in f :
                    line = line.strip()
                    queryy+=line


    return queryy