import subprocess
import os
import json
import sys

databaseName = str("src/files/"+sys.argv[1])

def generateERD():
    currendirectory = os.getcwd()+"/"
    subprocess.run("erdot graphoutput.json",shell=True, check=True)
    subprocess.run("dot -Tpng graphoutput.dot -o "+sys.argv[1]+"_ERD.png",shell=True, check=True)

def generateJsonForDatabase(databaseName):
    construct_json=""
    relations={}
    count=0
    path_to_json = databaseName+'/'
    json_files = [pos_json for pos_json in os.listdir(path_to_json) if pos_json.endswith('.json')]
    json_filesLength=len(json_files)
    filecount=0
    construct_json+="{\n\"tables\":{\n"
    for file in json_files:
        filecount=filecount+1
        with open(path_to_json+file,'r') as jsonFile:
            jsonDict = json.load(jsonFile)
            i=1
            count = count+1
            construct_json+="\""+jsonDict["tablename"]+"\":{\n"
            for  value in jsonDict["columnlist"]:
                value = dict(value)
                for columnName,datatype in dict(value).items():
                            construct_json+="\""+columnName+"\":\""+datatype+"\""
                            if (i<len(value)):
                                i=i+1
                                construct_json+=","
                            construct_json+="\n"
                if(filecount<json_filesLength):
                        construct_json+="\n},"
                else:
                        construct_json+="\n}"
            if "relations" in jsonDict:
                relations = jsonDict["relations"]
    construct_json+="},"
    if len(relations)>0:
        construct_json+="\n\"relations\":"
        for item in relations:
            print(len(item))
            j=1
            construct_json+="["
            for innerItem in item:
                    construct_json+="\""+str(innerItem)
                    if(j<len(item)):
                       construct_json+="\","
                       j=j+1
            construct_json+="\"]"
        construct_json+=",\n"
    else:
        construct_json+="\n\"relations\":[],\n"

    construct_json+="\"rankAdjustments\":\"\",\n"
    construct_json+= "\"label\":\"\"\n}"
    # print(construct_json)
    with open("graphoutput.json","w+") as jsonFile:
        jsonFile.write(construct_json)

generateJsonForDatabase(databaseName)
generateERD()
