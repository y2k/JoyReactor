package y2k.joyreactor.services.repository;

import y2k.joyreactor.Attachment;

/**
 * Created by y2k on 07/12/15.
 */
@Deprecated
public class AttachmentsQuery extends Repository.Query<Attachment> {

    private int postId;

    public AttachmentsQuery(int postId) {
        this.postId = postId;
    }

    @Override
    public boolean compare(Attachment attachment) {
        return attachment.getPostId() == postId;
    }
}