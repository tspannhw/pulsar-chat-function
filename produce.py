import pulsar

client = pulsar.Client('pulsar://127.0.0.1:6650')
# client = pulsar.Client('pulsar://nvidia-desktop:6650')
# 127.0.0.1:6650')
# //nvidia-desktop:6650')

producer = client.create_producer('persistent://public/default/chat')

for i in range(5):
    producer.send(('This is horrible, it makes me angry, what is everything bad and wrong.%d' % i).encode('utf-8'))
    producer.send(('This is great, awesome.   I love pulsar. %d' % i).encode('utf-8'))

client.close()
