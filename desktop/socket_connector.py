import json
from json import JSONDecodeError
from math import sin, cos, sqrt
from threading import Thread

import pyautogui
import requests
import websocket


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

# Key bindings (values are according to the key names in pyautogui)
up_binding = "w"
left_binding = "a"
down_binding = "s"
right_binding = "d"
shift_binding = ""
btn_a_binding = ""
btn_b_binding = "shiftleft"
btn_c_binding = "space"
btn_p_binding = "tab"
btn_n_binding = "escape"

# Key binary codes
# (to allow to easily combine keys and then check if that combination is already pressed on update cycle)
NONE = 0
UP = 1
LEFT = 1 << 1
RIGHT = 1 << 2
DOWN = 1 << 3
current_keys = 0

joy_vec = Vector2()
# Support vectors
primary_support_vecs = [Vector2(1, 0), Vector2(0, 1), Vector2(-1, 0), Vector2(0, -1)]
secondary_support_vecs = [Vector2(cos(45), sin(45)), Vector2(cos(135), sin(135)),
                          Vector2(cos(-45), sin(-45)), Vector2(cos(-135), sin(-135))]


def movement_key_press(key_code):
    global current_keys
    if key_code == current_keys:
        return

    print(key_code, bin(key_code))

    if key_code & UP:
        if not current_keys & UP:
            pyautogui.keyDown(up_binding)
    elif current_keys & UP:
        pyautogui.keyUp(up_binding)

    if key_code & LEFT:
        if not current_keys & LEFT:
            pyautogui.keyDown(left_binding)
    elif current_keys & LEFT:
        pyautogui.keyUp(left_binding)

    if key_code & DOWN:
        if not current_keys & DOWN:
            pyautogui.keyDown(down_binding)
    elif current_keys & DOWN:
        pyautogui.keyUp(down_binding)

    if key_code & RIGHT:
        if not current_keys & RIGHT:
            pyautogui.keyDown(right_binding)
    elif current_keys & RIGHT:
        pyautogui.keyUp(right_binding)

    current_keys = key_code


def processStickInput():
    while True:
        if joy_vec.x == 0 and joy_vec.y == 0:
            movement_key_press(NONE)
            continue
        elif joy_vec.x == 1:
            movement_key_press(RIGHT)
            continue
        elif joy_vec.y == 1:
            movement_key_press(UP)
            continue
        elif joy_vec.x == -1:
            movement_key_press(LEFT)
            continue
        elif joy_vec.y == -1:
            movement_key_press(DOWN)
            continue

        vec_distances = {vec.distance(joy_vec): vec for vec in primary_support_vecs}
        sec_vec_distances = {vec.distance(joy_vec): vec for vec in secondary_support_vecs}

        closest_primary_vec = vec_distances[min(vec_distances.keys())]
        closest_secondary_vec = sec_vec_distances[min(sec_vec_distances.keys())]

        primary_joy_distance = closest_primary_vec.distance(joy_vec)
        secondary_joy_distance = closest_secondary_vec.distance(joy_vec)

        # Note - the secondary vectors are right in the middle between two axes (or primary vectors in this case)
        combine_dirs = secondary_joy_distance < primary_joy_distance

        if joy_vec.x > 0 and joy_vec.y > 0:  # Q1
            if combine_dirs:
                movement_key_press(UP | RIGHT)
            elif closest_primary_vec.x == 0 or closest_primary_vec.y == 1:
                movement_key_press(UP)
            else:
                movement_key_press(RIGHT)
        elif joy_vec.x < 0 and joy_vec.y > 0:  # Q2
            if combine_dirs:
                movement_key_press(UP | LEFT)
            elif closest_primary_vec.x == 0 or closest_primary_vec.y == 1:
                movement_key_press(UP)
            else:
                movement_key_press(LEFT)
        elif joy_vec.x < 0 and joy_vec.y < 0:  # Q3
            if combine_dirs:
                movement_key_press(DOWN | LEFT)
            elif closest_primary_vec.x == 0 or closest_primary_vec.y == -1:
                movement_key_press(DOWN)
            else:
                movement_key_press(LEFT)
        elif joy_vec.x > 0 and joy_vec.y < 0:  # Q4
            if combine_dirs:
                movement_key_press(DOWN | RIGHT)
            elif closest_primary_vec.x == 0 or closest_primary_vec.y == -1:
                movement_key_press(DOWN)
            else:
                movement_key_press(RIGHT)


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
            pyautogui.keyDown(btn_b_binding)
        else:
            pyautogui.keyUp(btn_b_binding)

    if "btnC" in jsonObj:
        if jsonObj.get("btnC"):
            pyautogui.keyDown(btn_c_binding)
        else:
            pyautogui.keyUp(btn_c_binding)

    if "btnP" in jsonObj:
        if jsonObj.get("btnP"):
            pyautogui.keyDown(btn_p_binding)
        else:
            pyautogui.keyUp(btn_p_binding)

    if "btnN" in jsonObj:
        if jsonObj.get("btnN"):
            pyautogui.keyDown(btn_n_binding)
        else:
            pyautogui.keyUp(btn_n_binding)

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
