import csv
from py4j.java_gateway import JavaGateway
import subprocess
import sys

#not required
def register(userName, password):
    credentialDict={}
    credentialDict[userName]=password
    with open('credentials.csv','r') as credentials:
       mydict =  dict(csv.reader(credentials))
    if userName in list(mydict.keys()):
         print("User already registered")
    else:
        with open('credentials.csv','a+') as credentialsFile:
            writer = csv.writer(credentialsFile)
            for key,value in credentialDict.items():
                writer.writerow([key,value])
                print("user registered successfully")

def authenticate(userName, password):
    with open('credentials.csv') as csvfile:
        records = csv.reader(csvfile)
        mydict = dict(records)
        if userName in mydict and password == mydict[userName]:
            subprocess.call("java test", stderr=subprocess.PIPE)
        print("not authenticated")

def operationtoPerform(choice, userName,password):
    if choice == '1':
        register(userName,password)
        return
    if choice== '2':
        authenticate(userName,password)
        return
    else:
        print("invalid choice")


print("operations available 1. registration 2. authentication ")
choice=input("Enter your choice ")
userName = input("Enter your username ")
password = input("Enter your password ")
operationtoPerform(choice, userName,password)

