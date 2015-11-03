package y2k.joyreactor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Observable;
import y2k.joyreactor.common.PostGenerator;
import y2k.joyreactor.requests.PostsForTagRequest;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by y2k on 03/11/15.
 */
public class PostListSynchronizerTest {

    @Mock
    private Repository<Post> repository;

    @Mock
    private PostsForTagRequest.Factory requestFactory;
    @Mock
    private PostsForTagRequest request;

    private PostListSynchronizer synchronizer;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(requestFactory.make(anyString(), anyString())).thenReturn(request);

        synchronizer = new PostListSynchronizer(repository, requestFactory);
    }

    @Test
    public void test() throws Exception {
        when(request.getPosts()).thenReturn(PostGenerator.getMockFirstPage(0));
        when(request.requestAsync()).thenReturn(Observable.just(null));
        when(repository.queryAsync()).thenReturn(Observable.just(Collections.emptyList()));

        boolean actual = synchronizer.preloadNewPosts().toBlocking().last();
        verify(request).requestAsync();
        assertFalse(actual);

        synchronizer.applyNew().toBlocking().last();
        verify(repository).replaceAll(PostGenerator.getMockFirstPage(0));
    }
}