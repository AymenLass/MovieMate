from flask import Flask, request , send_file
import os
from inference import main

app = Flask(__name__)

def  delete_all_files():
    directory = 'C:/response_stocker/'
    try :
        files = os.listdir(directory)
        for filename in files :
            file_path = os.path.join(directory , filename)
            if os.path.isfile(file_path) :
                os.remove(file_path)

        return 'All files deleted successfully', 200
    except Exception as e:
        return 'Error deleting files: {}'.format(str(e)), 500

def  delete_all_files2():
    directory = 'C:/suggestion_stocker/'
    try :
        files = os.listdir(directory)
        for filename in files :
            file_path = os.path.join(directory , filename)
            if os.path.isfile(file_path) :
                os.remove(file_path)

        return 'All files deleted successfully', 200
    except Exception as e:
        return 'Error deleting files: {}'.format(str(e)), 500

@app.route('/', methods=['GET'])
def handle_call():
    return "Successfully Connected The Server"

@app.route('/download/<file_name>', methods=['GET'])
def download_file(file_name):
    files_directory = 'C:/suggestion_stocker/'
    file_path = files_directory + file_name

    try:
        return send_file(file_path, as_attachment=True)
    except FileNotFoundError:
        return "File not found", 404

@app.route('/upload', methods=['POST'])
def upload_file():
    if 'file' not in request.files:
        return 'No file part', 400

    file = request.files['file']

    if file.filename == '':
        return 'No selected file', 400


    # with open(file.filename, 'r') as f:
    #     file_content = f.read()
    #     print("content : " , file_content)



    if file.filename == "number_time.txt" :
        delete_all_files()
    file.save('C:/response_stocker/' + file.filename)
    # with open('C:/response_stocker/' + file.filename, 'r') as f:
    #     file_content = f.read()
    #     print("content : " , file_content)
    return 'File uploaded successfully', 200



@app.route('/trigger_main', methods=['POST'])
def trigger_main():
    # Call the main function
    main()
    return 'Main function triggered successfully'



@app.route('/reset_server' , methods = ['GET'])
def reset_server() :
    response, status_code = delete_all_files2()
    return response, status_code


if __name__ == '__main__':
    app.run(host="0.0.0.0", port=5000, debug=True)

