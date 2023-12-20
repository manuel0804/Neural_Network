package org.example;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class DiabetesNeuralNetwork {
    private final double[][] trainingInputs;
    private final double[][] trainingOutputs;
    private final double learningRate = 0.01;
    private final int numEpochs = 1000;

    private int numInputs, numHiddenNodes, numOutputs, numTrainingSets;

    public DiabetesNeuralNetwork(double[][] trainingInputs, double[][] trainingOutputs){
        this.trainingInputs = trainingInputs;
        this.trainingOutputs = trainingOutputs;
        this.numInputs = trainingInputs[0].length;
        this.numHiddenNodes = 500; // Adjust the number as needed
        this.numOutputs = trainingOutputs[0].length;
        this.numTrainingSets = trainingInputs.length;
    }

    private double initWeights(){
        return new Random().nextDouble();
    }

    private double sigmoid(double x){
        return 1 / (1 + Math.exp(-x));
    }

    private double sigmoidDerivative(double x){
        return sigmoid(x) * (1 - sigmoid(x));
    }

    private void shuffle(int[] array){
        Random rand = new Random();
        int n = array.length;

        for (int i = n - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);

            // Swap array[i] and array[j]
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public void train(){

        double[] hiddenLayer = new double[numHiddenNodes];
        double[] outputLayer = new double[numOutputs];

        double[] hiddenLayerBias = new double[numHiddenNodes];
        double[] outputLayerBias = new double[numOutputs];

        double[][] hiddenWeights = new double[numInputs][numHiddenNodes];
        double[][] outputWeights = new double[numHiddenNodes][numOutputs];

        // Initialize weights
        for(int i = 0; i < numInputs; i++){
            for(int j = 0; j < numHiddenNodes; j++){
                hiddenWeights[i][j] = initWeights();
            }
        }

        for(int i = 0; i < numHiddenNodes; i++){
            for(int j = 0; j < numOutputs; j++){
                outputWeights[i][j] = initWeights();
            }
        }

        // Initialize biases
        for(int i = 0; i < numHiddenNodes; i++){
            hiddenLayerBias[i] = initWeights();
        }


        int[] trainingSetOrder = IntStream.range(0, numTrainingSets).toArray();



        // Training
        for(int epoch = 0; epoch < numEpochs; epoch++){

            shuffle(trainingSetOrder);

            for(int x = 0; x < numTrainingSets; x++){

                int i = trainingSetOrder[x];

                // Forward pass
                for (int j=0; j<numHiddenNodes; j++) {
                    double activation = hiddenLayerBias[j];
                    for (int k=0; k<numInputs; k++) {
                        activation += trainingInputs[i][k] * hiddenWeights[k][j];
                    }
                    hiddenLayer[j] = sigmoid(activation);
                }


                for (int j=0; j<numOutputs; j++) {
                    double activation = outputLayerBias[j];
                    for (int k=0; k<numHiddenNodes; k++) {
                        activation += hiddenLayer[k] * outputWeights[k][j];
                    }
                    outputLayer[j] = sigmoid(activation);
                }

                //prints the input
                System.out.printf("Input: %s\n", Arrays.toString(trainingInputs[i]));
                System.out.printf("Output: %.6f,  Expected Output: %d\n\n", outputLayer[0], (int)trainingOutputs[i][0]);



                // Backpropagation
                double[] deltaOutput = new double[numOutputs];
                for (int j=0; j<numOutputs; j++) {
                    double errorOutput = (trainingOutputs[i][j] - outputLayer[j]);
                    deltaOutput[j] = errorOutput * sigmoidDerivative(outputLayer[j]);
                }

                double[] deltaHidden = new double[numHiddenNodes];
                for (int j=0; j<numHiddenNodes; j++) {
                    double errorHidden = 0;
                    for (int k=0; k<numOutputs; k++) {
                        errorHidden += deltaOutput[k] * outputWeights[j][k];
                    }
                    deltaHidden[j] = errorHidden * sigmoidDerivative(hiddenLayer[j]);
                }

                // Update weights
                for (int j=0; j<numOutputs; j++){
                    outputLayerBias[j] += deltaOutput[j] * learningRate;
                    for (int k=0; k<numHiddenNodes; k++) {
                        outputWeights[k][j] += hiddenLayer[k] * deltaOutput[j] * learningRate;
                    }
                }

                for (int j=0; j<numHiddenNodes; j++) {
                    hiddenLayerBias[j] += deltaHidden[j] * learningRate;
                    for(int k=0; k<numInputs; k++) {
                        hiddenWeights[k][j] += trainingInputs[i][k] * deltaHidden[j] * learningRate;
                    }
                }
            }
        }
    }
}
