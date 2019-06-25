import serial
import time
from firebase import firebase

arduinoData = serial.Serial('COM4', 9600)
firebase = firebase.FirebaseApplication('https://android-text-64647.firebaseio.com/')

generaldatacount = 1

def valuePutter(data,count):
    result = firebase.put('datas', count, data)

if __name__ == '__main__':
    if arduinoData.isOpen:
        while True:
            size = arduinoData.inWaiting();
            if size:
                data = arduinoData.read(size)
                valuePutter(data, generaldatacount)
                print data
            else:
                print 'no data'
            time.sleep(1)
    else:
        print 'Serial not open'










