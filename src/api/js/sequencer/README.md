# UDP Sequencer for Replicas

This UDP sequencer is used when running the java, js and python replicas. You can run one or all of the replica implementations, then run this sequencer and send images as shown below.

TODO: Implement for live camera feed as well (color and depth).

## Usage

Run it as follows:

```
npm install
npm run sequencer
```

## Testing with Replicas

Here are the steps you need to follow to test the replica implementation:

1) Run all the replicas from their respective folder, in separate terminals e.g:

From src/api/python:
```
python udp_receiver.py
```

From src/api/js/opencv-replica:
```
npm run replica
```

2) Run the js frontend which will display the end result:

From src/api/js/opencv-replica:
```
npm run frontend
```

3) Run the UDP sequencer from this directory:
```
npm run sequencer
```

4) Finally, send frames by opening your browser and entering the following:

```
http://localhost:8085/1/canny
http://localhost:8085/2/contour
...
```

The result should be updated for each frame sent at http://localhost:3000

### Note:

If testing with more than one replica, change the following variable to false in app.js:

```
let singleReplica = false;
```

