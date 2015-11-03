package y2k.joyreactor.presenters;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import rx.Observable;
import y2k.joyreactor.Post;
import y2k.joyreactor.PostMerger;
import y2k.joyreactor.Repository;
import y2k.joyreactor.requests.PostsForTagRequest;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by y2k on 11/2/15.
 */
public class PostListPresenterTest {

    Post[] FIRST_PAGE = new Post[10];
    Post[] NEXT_PAGE = new Post[10];

    {
        for (int i = 0; i < FIRST_PAGE.length; i++)
            FIRST_PAGE[i] = makePost("" + i);
        for (int i = 0; i < NEXT_PAGE.length; i++)
            NEXT_PAGE[i] = makePost("" + (FIRST_PAGE.length + i));
    }

    private Post makePost(String id) {
        Post p = new Post();
        p.id = id;
        return p;
    }

    @Mock
    PostListPresenter.View mockView;
    @Mock
    Repository<Post> mockRepository;

    @Mock
    PostsForTagRequest mockRequest;
    @Mock
    PostsForTagRequest.Factory mockFactory;

    @Mock
    PostMerger mockMerger;
    @Mock
    PostMerger.Fabric mockMergerFactory;

    @Captor
    ArgumentCaptor<List<Post>> captor;

    PostListPresenter presenter;

    @Before
    public void setUp() throws Exception {
        initialize();

        when(mockRequest.requestAsync()).thenAnswer(s -> {
            when(mockRequest.getPosts()).thenAnswer(s2 -> Arrays.asList(FIRST_PAGE));
            return Observable.just(null);
        });

        when(mockFactory.make(anyString(), anyString())).thenReturn(mockRequest);
        when(mockMergerFactory.make(any())).thenReturn(mockMerger);

//        PostMerger.Fabric mockMergerFactory = mock(PostMerger.Fabric.class);
        presenter = new PostListPresenter(mockView, mockRepository, mockFactory, mockMergerFactory);
    }

    private void initialize() {
        MockitoAnnotations.initMocks(this);

        when(mockRepository.queryAsync()).thenAnswer(s -> Observable.just(new ArrayList<>()));
        when(mockRepository.replaceAllAsync(any())).thenAnswer(s -> {
            List<Post> arg = (List<Post>) s.getArguments()[0];
            when(mockRepository.queryAsync()).thenReturn(Observable.just(new ArrayList<>(arg)));
            return Observable.just(null);
        });
    }

    @Test
    public void test() throws Exception {
        testConstructor();
        subTestLoadNext();
    }

    private void testConstructor() {
        verify(mockView).reloadPosts(captor.capture(), isNull(Integer.class));
        assertEquals(0, captor.getValue().size());

        verify(mockView).reloadPosts(captor.capture(), eq(10));
        assertEquals(10, captor.getValue().size());
    }

    private void subTestLoadNext() throws Exception {
        initialize();

        when(mockRequest.requestAsync()).thenAnswer(s -> {
            when(mockRequest.getPosts()).thenReturn(Arrays.asList(NEXT_PAGE));
            return Observable.just(null);
        });

        presenter.loadMore();

        verify(mockView).reloadPosts(captor.capture(), eq(20));
        assertEquals(20, captor.getValue().size());
    }
}