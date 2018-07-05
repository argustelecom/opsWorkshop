package ru.argustelecom.box;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.primefaces.context.RequestContext;

import javax.faces.context.ExternalContextWrapper;
import javax.faces.context.FacesContext;
import java.util.HashMap;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Служебный класс для мокирования FacesContext'а для unit-тестов
 *
 * @author d.khekk
 * @since 28.08.2017
 */
@PrepareForTest(RequestContext.class)
public abstract class ContextMocker extends FacesContext {
    private ContextMocker() {
    }

    private static final Release RELEASE = new Release();

    private static class Release implements Answer<Void> {
        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
            setCurrentInstance(null);
            return null;
        }
    }

    public static FacesContext mockFacesContext() {
        FacesContext context = Mockito.mock(FacesContext.class);
        setCurrentInstance(context);
        Mockito.doAnswer(RELEASE)
                .when(context)
                .release();
        RequestContext requestContext = Mockito.mock(RequestContext.class);
        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put(RequestContext.class.getName(), requestContext);
        when(context.getAttributes()).thenReturn(hashMap);
		when(context.getExternalContext()).thenReturn(Mockito.mock(ExternalContextWrapper.class));
		return context;
    }

    /**
     * Мочит RequestContext. для мокирования статических методов нужно добавить над тестовым классом
     * аннотацию @PrepareForTest(RequestContext.class)
     * @return mocked request context
     */
    public static RequestContext mockRequestContext() {
        mockStatic(RequestContext.class);

        RequestContext mockRequestContext = mock(RequestContext.class);
        doNothing().when(mockRequestContext).update(anyString());
        doNothing().when(mockRequestContext).execute(anyString());

        PowerMockito.when(RequestContext.getCurrentInstance()).thenReturn(mockRequestContext);
        return mockRequestContext;
    }
}
