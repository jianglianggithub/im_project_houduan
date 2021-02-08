package com.jl.entity;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.KurentoClient;
import org.kurento.client.MediaPipeline;
import org.kurento.client.WebRtcEndpoint;

@Slf4j
public class CallMediaPipeline {

    private MediaPipeline pipeline;
    private WebRtcEndpoint fromWebRtcEp;
    private WebRtcEndpoint toWebRtcEp;
    // 避免调用2次释放
    private boolean release;


    public CallMediaPipeline(KurentoClient kurento) {
        try {
            this.pipeline = kurento.createMediaPipeline();
            this.fromWebRtcEp = new WebRtcEndpoint.Builder(pipeline).build();
            this.toWebRtcEp = new WebRtcEndpoint.Builder(pipeline).build();

            this.fromWebRtcEp.connect(this.toWebRtcEp);
            this.toWebRtcEp.connect(this.fromWebRtcEp);
        } catch (Throwable t) {
            t.printStackTrace();
            if (this.pipeline != null) {
                pipeline.release();
            }
        }
    }

    public String generateSdpAnswerForCaller(String sdpOffer) {
        return fromWebRtcEp.processOffer(sdpOffer);
    }

    public String generateSdpAnswerForCallee(String sdpOffer) {
        return toWebRtcEp.processOffer(sdpOffer);
    }

    public void release() {
        if (pipeline != null && !release) {

            pipeline.release();
            release = true;
        } else {
            log.info("release = true pipeline 已经 release 过了 无需再次释放");
        }
    }

    public WebRtcEndpoint getFromWebRtcEp() {
        return fromWebRtcEp;
    }

    public WebRtcEndpoint getToWebRtcEp() {
        return toWebRtcEp;
    }

}
