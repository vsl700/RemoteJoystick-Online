import json
from json import JSONDecodeError
from threading import Thread

import pyautogui
import websocket
import requests

from math import sin, cos, sqrt


class Vector2:
    def __init__(self, *args):
        if len(args) < 2:
            self.x = self.y = 0
        else:
            self.x = args[0]
            self.y = args[1]

    def length(self):
        return sqrt(self.x ** 2 + self.y ** 2)

    def distance(self, vec):
        return Vector2(self.x - vec.x, self.y - vec.y).length()


pyautogui.FAILSAFE = False

jsonObj = {}
tickDuration = 0.01
joy_vec = Vector2()
# Support vectors
primary_support_vecs = [Vector2(1, 0), Vector2(0, 1), Vector2(-1, 0), Vector2(0, -1)]
secondary_support_vecs = [Vector2(cos(45), sin(45)), Vector2(cos(135), sin(135)),
                          Vector2(cos(-45), sin(-45)), Vector2(cos(-135), sin(-135))]


def on_error(ws, error):
    print(error)


def on_close(ws, close_status_code, close_msg):
    print("### closed ###")
    print(close_status_code)
    print(close_msg)


def on_open(ws):
    print("Opened connection")


def processStickInput():
    while True:
        if joy_vec.x == 0 and joy_vec.y == 0:
            pyautogui.keyUp("w")
            pyautogui.keyUp("a")
            pyautogui.keyUp("s")
            pyautogui.keyUp("d")
            continue
        elif joy_vec.x == 1:
            pyautogui.keyUp("w")
            pyautogui.keyUp("a")
            pyautogui.keyUp("s")
            pyautogui.keyDown("d")
            continue
        elif joy_vec.y == 1:
            pyautogui.keyDown("w")
            pyautogui.keyUp("a")
            pyautogui.keyUp("s")
            pyautogui.keyUp("d")
            continue
        elif joy_vec.x == -1:
            pyautogui.keyUp("w")
            pyautogui.keyDown("a")
            pyautogui.keyUp("s")
            pyautogui.keyUp("d")
            continue
        elif joy_vec.y == -1:
            pyautogui.keyUp("w")
            pyautogui.keyUp("a")
            pyautogui.keyDown("s")
            pyautogui.keyUp("d")
            continue

        vec_distances = {vec.distance(joy_vec): vec for vec in primary_support_vecs}
        sec_vec_distances = {vec.distance(joy_vec): vec for vec in secondary_support_vecs}

        closest_primary_vec = vec_distances[min(vec_distances.keys())]
        closest_secondary_vec = sec_vec_distances[min(sec_vec_distances.keys())]

        primary_joy_distance = closest_primary_vec.distance(joy_vec)
        secondary_joy_distance = closest_secondary_vec.distance(joy_vec)
        combine_dirs = secondary_joy_distance < primary_joy_distance

        if joy_vec.x > 0 and joy_vec.y > 0:  # Q1
            if combine_dirs:
                pyautogui.keyDown("w")
                pyautogui.keyUp("a")
                pyautogui.keyUp("s")
                pyautogui.keyDown("d")
            elif closest_primary_vec.x == 0 or closest_primary_vec.y == 1:
                pyautogui.keyDown("w")
                pyautogui.keyUp("a")
                pyautogui.keyUp("s")
                pyautogui.keyUp("d")
            else:
                pyautogui.keyUp("w")
                pyautogui.keyUp("a")
                pyautogui.keyUp("s")
                pyautogui.keyDown("d")
        elif joy_vec.x < 0 and joy_vec.y > 0:  # Q2
            if combine_dirs:
                pyautogui.keyDown("w")
                pyautogui.keyDown("a")
                pyautogui.keyUp("s")
                pyautogui.keyUp("d")
            elif closest_primary_vec.x == 0 or closest_primary_vec.y == 1:
                pyautogui.keyDown("w")
                pyautogui.keyUp("a")
                pyautogui.keyUp("s")
                pyautogui.keyUp("d")
            else:
                pyautogui.keyUp("w")
                pyautogui.keyDown("a")
                pyautogui.keyUp("s")
                pyautogui.keyUp("d")
        elif joy_vec.x < 0 and joy_vec.y < 0:  # Q3
            if combine_dirs:
                pyautogui.keyUp("w")
                pyautogui.keyDown("a")
                pyautogui.keyDown("s")
                pyautogui.keyUp("d")
            elif closest_primary_vec.x == 0 or closest_primary_vec.y == -1:
                pyautogui.keyUp("w")
                pyautogui.keyUp("a")
                pyautogui.keyDown("s")
                pyautogui.keyUp("d")
            else:
                pyautogui.keyUp("w")
                pyautogui.keyDown("a")
                pyautogui.keyUp("s")
                pyautogui.keyUp("d")
        elif joy_vec.x > 0 and joy_vec.y < 0:  # Q4
            if combine_dirs:
                pyautogui.keyUp("w")
                pyautogui.keyUp("a")
                pyautogui.keyDown("s")
                pyautogui.keyDown("d")
            elif closest_primary_vec.x == 0 or closest_primary_vec.y == -1:
                pyautogui.keyUp("w")
                pyautogui.keyUp("a")
                pyautogui.keyDown("s")
                pyautogui.keyUp("d")
            else:
                pyautogui.keyUp("w")
                pyautogui.keyUp("a")
                pyautogui.keyUp("s")
                pyautogui.keyDown("d")


def processButtonInput():
    if "shift" in jsonObj:
        if jsonObj.get("shift"):
            pyautogui.mouseDown(button=pyautogui.LEFT)
        else:
            pyautogui.mouseUp(button=pyautogui.LEFT)

    if "btnA" in jsonObj:
        if jsonObj.get("btnA"):
            pyautogui.mouseDown(button=pyautogui.RIGHT)
        else:
            pyautogui.mouseUp(button=pyautogui.RIGHT)

    if "btnB" in jsonObj:
        if jsonObj.get("btnB"):
            pyautogui.keyDown("shiftleft")
        else:
            pyautogui.keyUp("shiftleft")

    if "btnC" in jsonObj:
        if jsonObj.get("btnC"):
            pyautogui.keyDown("space")
        else:
            pyautogui.keyUp("space")

    if "btnP" in jsonObj:
        if jsonObj.get("btnP"):
            pyautogui.keyDown("tab")
        else:
            pyautogui.keyUp("tab")

    if "btnN" in jsonObj:
        if jsonObj.get("btnN"):
            pyautogui.keyDown("escape")
        else:
            pyautogui.keyUp("escape")

    if "joyX" in jsonObj:
        joy_vec.x = jsonObj.get("joyX")

    if "joyY" in jsonObj:
        joy_vec.y = jsonObj.get("joyY")


PORT = requests.get("http://localhost:8080/RemoteJoystickDynWebProject/new_port").text
print("port:", PORT)

websocket.enableTrace(True)
ws = websocket.WebSocket()
ws.connect("ws://localhost:8080/RemoteJoystickDynWebProject/websocket/rj?port={port}".format(port=PORT))

inputProcessThread = Thread(daemon=True, target=processStickInput)
inputProcessThread.start()

while True:
    data = ws.recv_frame().data
    print(data)

    try:
        jsonObj = json.loads(data)

        processButtonInput()
    except JSONDecodeError:
        pass
