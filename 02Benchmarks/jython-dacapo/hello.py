
def test():
    a = 0
    for x in range(100):
        a+=x
    print(a)
    return a
 
 
if __name__ == "__main__":
    print("hello")
    b = test()
    print(b)
