from flask import Flask, jsonify, request
import pymongo
import json


# Initialize app
app = Flask(__name__)

#Database
client = pymongo.MongoClient('mongodb://127.0.0.1', 27017)
db = client.newDB

@app.route('/')
def home():
    return "Home"

@app.route('/add_fav', methods=['POST'])
def add_data():
    if request.method == "POST":
        
        data = json.loads(request.data)

        print(data)
        

    if db.items.insert(data):
        return json.dumps({"ok" : 200})
    else:
        return json.dumps({"failed" : 400})

        
@app.route('/getFavs')
def getFavs():
    userID = request.args.get("userID")
    print(userID)
    cursor = db.items.find({"userID":userID})
    response = {"results": []}
    for doc in cursor:
        doc.pop("_id")
        if doc not in response["results"]:
            response["results"].append(doc)
    
    return response


@app.route('/removeFav', methods=['POST'])
def removeFav():
    
    print("Questi sono i dati da rimuovere")
    print(request.data)
    data = json.loads(request.data)
    
        

    if db.items.delete_one(data):
        return json.dumps({"ok" : 200})
    else:
        return json.dumps({"failed" : 400})



app.run(debug=True)



