package y2k.joyreactor.services.synchronizers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import rx.Observable;
import y2k.joyreactor.Post;
import y2k.joyreactor.Tag;
import y2k.joyreactor.common.PostGenerator;
import y2k.joyreactor.services.requests.PostsForTagRequest;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by y2k on 03/11/15.
 */
public class PostListFetcherTest {

    @Mock
    private PostSubRepositoryForTag repository;
    @Mock
    private PostsForTagRequest.Factory requestFactory;
    @Mock
    private PostsForTagRequest request;

    private PostListFetcher synchronizer;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(requestFactory.make(any(), anyString())).thenReturn(request);

        synchronizer = new PostListFetcher(new Tag(), repository, requestFactory);
    }

    @Test
    public void testLoadNextPageWithUnion() {
        final int pageSize = PostGenerator.PAGE_SIZE;
        loadFirstPage();

        for (int n = 0; n < 9; n++) {
            when(repository.queryAsync()).thenReturn(Observable.just(PostGenerator.getPostRange(0, pageSize + n * (pageSize / 2))));
            when(request.getPosts()).thenReturn(PostGenerator.getPostRange(pageSize / 2 + n * (pageSize / 2), pageSize));

            synchronizer.loadNextPage().toBlocking().last();

            verify(repository).replaceAllAsync(argThat(new ListArgumentMatcher(PostGenerator.getPostRange(0, pageSize + pageSize / 2 + n * (pageSize / 2)))));
            assertEquals(pageSize / 2 * (3 + n), (int) synchronizer.getDivider());
        }
    }

    @Test
    public void testLoadNextPage() {
        loadFirstPage();

        for (int page = 0; page < 9; page++) {
            when(repository.queryAsync()).thenReturn(Observable.just(PostGenerator.getPageRange(0, page + 1)));
            when(request.getPosts()).thenReturn(PostGenerator.getPageRange(page + 1, 1));

            synchronizer.loadNextPage().toBlocking().last();

            verify(repository).replaceAllAsync(argThat(new ListArgumentMatcher(PostGenerator.getPageRange(0, page + 2))));
            assertEquals(PostGenerator.PAGE_SIZE * (2 + page), (int) synchronizer.getDivider());
        }
    }

    private void loadFirstPage() {
        createInitState();
        when(repository.replaceAllAsync(anyListOf(Post.class))).thenReturn(Observable.just(null));

        synchronizer.preloadNewPosts();
        synchronizer.applyNew().toBlocking().last();
    }

    @Test
    public void testPreloadNewPosts() throws Exception {
        createInitState();

        boolean actual = synchronizer.preloadNewPosts().toBlocking().last();

        assertNull(synchronizer.getDivider());
        verify(request).requestAsync();
        assertFalse(actual);
    }

    @Test
    public void testApplyNew() throws Exception {
        createInitState();
        synchronizer.preloadNewPosts();

        when(repository.replaceAllAsync(anyListOf(Post.class))).thenReturn(Observable.just(null));
        synchronizer.applyNew().toBlocking().last();

        verify(repository).replaceAllAsync(argThat(new ListArgumentMatcher(PostGenerator.getPageRange(0, 1))));
        assertEquals(10, (int) synchronizer.getDivider());
    }

    private void createInitState() {
        when(request.getPosts()).thenReturn(PostGenerator.getPageRange(0, 1));
        when(request.requestAsync()).thenReturn(Observable.just(null));
        when(repository.queryAsync()).thenReturn(Observable.just(Collections.emptyList()));
    }

    private static class ListArgumentMatcher extends ArgumentMatcher<List<Post>> {

        private List<Post> expected;

        private ListArgumentMatcher(List<Post> expected) {
            this.expected = expected;
        }

        @Override
        public boolean matches(Object argument) {
            List<Post> actual = (List<Post>) argument;
            if (expected.size() != actual.size()) return false;
            for (int i = 0; i < actual.size(); i++)
                if (!Objects.equals(expected.get(i).serverId, actual.get(i).serverId)) return false;
            return true;
        }
    }
}