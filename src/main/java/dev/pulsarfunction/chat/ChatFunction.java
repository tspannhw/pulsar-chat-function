package dev.pulsarfunction.chat;

import ai.djl.Device;
import ai.djl.repository.Artifact;
import ai.djl.training.util.DownloadUtils;
import org.apache.pulsar.functions.api.Context;
import org.apache.pulsar.functions.api.Function;

import ai.djl.Application;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static ai.djl.repository.zoo.ModelZoo.*;


/**
 text function for chat

**/
public class ChatFunction implements Function<String, String> {

    /**
     *
     * @param message
     * @return
     * @throws IOException
     * @throws TranslateException
     * @throws ModelException
     */
    private String predict(String message) throws IOException, TranslateException, ModelException {
        String result = "Neutral";

        if ( message == null || message.trim().length() <=0 ) {
            return result; 
        }
/**
 * 13:26:56.440 [main] INFO  ai.djl.pytorch.jni.LibUtils - Downloading https://djl-ai.s3.amazonaws.com/publish/pytorch-1.6.0/cpu/osx/native/lib/libc10.dylib.gz ...
 * 13:26:56.564 [main] INFO  ai.djl.pytorch.jni.LibUtils - Downloading https://djl-ai.s3.amazonaws.com/publish/pytorch-1.6.0/cpu/osx/native/lib/libiomp5.dylib.gz ...
 * 13:26:56.719 [main] INFO  ai.djl.pytorch.jni.LibUtils - Downloading https://djl-ai.s3.amazonaws.com/publish/pytorch-1.6.0/cpu/osx/native/lib/libtensorpipe.dylib.gz ...
 * 13:26:56.888 [main] INFO  ai.djl.pytorch.jni.LibUtils - Downloading https://djl-ai.s3.amazonaws.com/publish/pytorch-1.6.0/cpu/osx/native/lib/libtorch.dylib.gz ...
 * 13:26:56.927 [main] INFO  ai.djl.pytorch.jni.LibUtils - Downloading https://djl-ai.s3.amazonaws.com/publish/pytorch-1.6.0/cpu/osx/native/lib/libtorch_cpu.dylib.gz ...
 *
 */
//        Criteria<String, Classifications> criteria =
//                Criteria.builder()
//                        .optApplication(Application.NLP.SENTIMENT_ANALYSIS)
//                        .optEngine( "PyTorch" )
//                        .setTypes(String.class, Classifications.class)
//                        .optProgress(new ProgressBar())
//                        .optModelUrls("https://djl-ai.s3.amazonaws.com/publish/pytorch-1.6.0/cpu/osx/native/lib/")
//                        .build();
//
//        Map<Application, List<Artifact>> applicationListMap = ModelZoo.listModels();
//        for (model:
//             ModelZoo.listModels();) {
//            System.err.println("Model:"+ model);
//        }

        // file://

        //System.out.println("Model Count:" + ModelZoo.listModels().size() );
//                        .optModelUrls("https://djl-ai.s3.amazonaws.com/publish/pytorch-1.6.0/cpu/osx/native/lib/libtorch_cpu.dylib.gz")
//                .optModelUrls("https://djl-ai.s3.amazonaws.com/publish/pytorch-1.6.0/cpu/osx/native/lib/libtensorpipe.dylib.gz")
//                .optModelUrls("https://djl-ai.s3.amazonaws.com/publish/pytorch-1.6.0/cpu/osx/native/lib/libiomp5.dylib.gz")
//                .optModelUrls("https://djl-ai.s3.amazonaws.com/publish/pytorch-1.6.0/cpu/osx/native/lib/libtorch.dylib.gz")

// DownloadUtils.download("https://djl-ai.s3.amazonaws.com/mlrepo/model/nlp/question_answer/ai/djl/pytorch/bertqa/0.0.1/bert-base-uncased-vocab.txt.gz", "build/pytorch/bertqa/vocab.txt", new ProgressBar());
//                        .optModelUrls("file:///Users/tspann/Documents/code/pulsar-chat-function/distilbert.tar.gz")
//                        .optDevice(Device.cpu())
        //                        .optModelUrls("file:///Users/tspann/Documents/code/pulsar-chat-function/distilbert.tar.gz")
     //   DownloadUtils.download("file:///Users/tspann/Documents/code/pulsar-chat-function/distilbert.tar.gz", "");
//                        .optEngine( "PyTorch" )

        Criteria<String, Classifications> criteria =
                Criteria.builder()
                        .optApplication(Application.NLP.SENTIMENT_ANALYSIS)
                        .setTypes(String.class, Classifications.class)
                        .optProgress(new ProgressBar())
                        .optDevice(Device.cpu())
                        .optEngine( "PyTorch" )
                        .build();

//        Criteria<Image, Classifications> criteria = Criteria.builder()
//                .setTypes(Image.class, Classifications.class) // defines input and output data type
//                .optTranslator(ImageClassificationTranslator.builder().setSynsetArtifactName("synset.txt").build())
//                .optModelUrls("file:///var/models/my_resnet50") // search models in specified path
//                .optModelName("resnet50") // specify model file prefix
//                .build();

        double probPositive = -1;
        double probNegative = -1;

        try (ZooModel<String, Classifications> model = criteria.loadModel()) {
            try (Predictor<String, Classifications> predictor = model.newPredictor()) {
                Classifications classifications = predictor.predict(message);
                if ( classifications == null) {
                    return result;
                }
                else {

                    if ( classifications.items() != null && classifications.items().size() > 0) {
                        for (Classifications.Classification classification : classifications.items()) {
                            try {
                                if (classification != null) {

                                    if ( classification.getClassName().equalsIgnoreCase( "positive" )) {
                                        probPositive = classification.getProbability();
                                    }
                                    else if ( classification.getClassName().equalsIgnoreCase( "negative" )) {
                                        probNegative = classification.getProbability();
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                result = "Neutral";
                            }
                        }
                    }
                }
            }
        }

        // System.out.println("ProbNeg:" + probNegative + " ProbPos:" + probPositive);

        if (probPositive>probNegative ) {
            result = "Positive";
        }
        else if ( probNegative > probPositive ) {
            result = "Negative";
        }
        return result;
    }


    /**
test 
*/
    public String process(String input, Context context) {
        if (input == null) {
            return "";
        }

        String sentiment = "Neutral";
        try {
            sentiment = predict(input);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TranslateException e) {
            e.printStackTrace();
        } catch (ModelException e) {
            e.printStackTrace();
        }

        if ( context != null && context.getLogger() != null) {
//            if ( context.getInputTopics().size() > 0) {
//                Collection<String> inputTopics = context.getInputTopics();
//                for (String inputTopic :inputTopics ) {
//                    context.getLogger().info("T:" + inputTopic);
//                }
//            }

            context.getLogger().info("LOG:" + context.getFunctionName() + ":" + input + ":" + sentiment);
        }
        return String.format( "%s", sentiment);
    }
} 
