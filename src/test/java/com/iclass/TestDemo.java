package com.iclass;

import com.sun.org.apache.xerces.internal.impl.dv.xs.AnyURIDV;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import org.mockito.Mock;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.sql.rowset.spi.SyncProvider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * junit-mokito
 * <p>
 * Created by yang.tang on 2017/2/17 17:03.
 */
public class TestDemo extends BaseTestCase{

    @Test
    public void verify_behaviour() {
        //模拟创建一个List对象
        List mockedList = mock(List.class);
        //使用mock对象
        mockedList.add(new Object());
        //验证add(1) 和 clear()行为是否发生
        verify(mockedList).add(new Object());
    }

    @Test
    public void when_thenReturn() {
        //mock一个Iterator类
        Iterator iterator = mock(Iterator.class);
        //预设当iterator 调用next() 时，第一次返回hello ，第n次都返回 world
        when(iterator.next()).thenReturn("hello").thenReturn("world");
        //使用mock 对象
        String result = iterator.next() + " " + iterator.next() + " " + iterator.next();
        //验证结果
        assertEquals("hello world world", result);
    }

    @Test(expected = IOException.class)
    public void when_thenThrow() throws IOException {
        OutputStream os = mock(OutputStream.class);
        OutputStreamWriter writer = new OutputStreamWriter(os);
        //预设当流关闭时抛出异常
        doThrow(new IOException()).when(os).close();
        os.close();
    }

    @Test
    public void with_arguruments() {
        Comparable comparable = mock(Comparable.class);
        //预设根据不同的参数返回不同的结果
        when(comparable.compareTo("test")).thenReturn(1);
        when(comparable.compareTo("foo")).thenReturn(2);
        //验证结果
        assertEquals(1, comparable.compareTo("test"));
        assertEquals(2, comparable.compareTo("foo"));
        //对于没有预设的情况会返回默认值
        assertEquals(0, comparable.compareTo("Not stub"));
    }

    @Test
    public void with_unspecified_arguments() {
        List list = mock(List.class);
        //匹配参数
        when(list.get(anyInt())).thenReturn(1);
        //自定义参数匹配
        when(list.contains(argThat(new ArgumentMatcher() {
            @Override
            public boolean matches(Object argument) {
                return argument == 1 || argument == 2;
            }
        }))).thenReturn(true);
        assertEquals(1, list.get(0));
        assertEquals(1, list.get(999));
        assertTrue(list.contains(1));
        assertTrue(!list.contains(3));
    }

    @Test
    public void verifying_number_of_invovations() {
        List<Integer> list = mock(List.class);
        list.add(1);
        list.add(2);
        list.add(2);
        list.add(3);
        list.add(3);
        list.add(3);
        //验证是否被调用一次,等效于下面的times(1)
        verify(list).add(1);
        verify(list, times(1)).add(1);

        verify(list, times(2)).add(2);
        verify(list, times(3)).add(3);

        verify(list, never()).add(4);

        verify(list, atLeastOnce()).add(1);

        verify(list, atLeast(2)).add(2);

        verify(list, atMost(3)).add(3);
    }

    @Test(expected = RuntimeException.class)
    public void doThrow_when() {
        List<Integer> list = mock(List.class);
        doThrow(new RuntimeException()).when(list).add(1);
        list.add(1);
    }

    @Test
    public void verification_in_order() {
        List list = mock(List.class);
        List list2 = mock(List.class);

        list.add(1);
        list2.add("hello");
        list.add(2);
        list2.add("world");

        //将需要排序的mock对象 放入Inorder
        InOrder order = inOrder(list, list2);

        //下面的代码不能颠倒顺序，验证执行顺序
        order.verify(list).add(1);
        order.verify(list2).add("hello");
        order.verify(list).add(2);
        order.verify(list2).add("world");

    }

    /**
     * 验证mock的对象没有产生任何动作
     */
    @Test
    public void verify_interaction() {
        List list = mock(List.class);
        List list2 = mock(List.class);
        List list3 = mock(List.class);
        list.add(1);
        verify(list).add(1);
        verify(list, never()).add(2);

        //产生了动作
//        list2.add(1);
//        list3.add(2);
        //验证零互动行为
        verifyZeroInteractions(list2, list3);
    }

    /**
     * 找出冗余的互动，即未被验证到的
     */
    @Test(expected = NoInteractionsWanted.class)
    public void find_redundant_interaction() {
//        List list = mock(List.class);
//        list.add(1);
//        list.add(2);
//        verify(list, times(2)).add(anyInt());
//        //检查是否有未被验证的互动行为，因为add(1)和add(2)都会被上面的anyInt()验证到
//        // 所以下面的代码会通过
//        verifyNoMoreInteractions(list);

        List list2 = mock(List.class);
        list2.add(1);
        list2.add(2);
        //检查是否有未被验证的互动行为，因为add(2)没有被验证
        //所以下面的代码会失败抛出异常
        verifyNoMoreInteractions(list2);
    }

    //使用注解来快速模拟
    @Mock
    private List shorthand_list;

    @Test
    public void shorthand() {
        shorthand_list.add(1);
        verify(shorthand_list).add(1);
    }

    /**
     * 连续调用
     * expected = RuntimeException.class 期望获得这样的一个错误
     * 如果发出就测试成功
     * 否则，测试失败
     */
    @Test(expected = RuntimeException.class)
    public void consecutive_calls() {
        //模拟连续调用返回期望值，如果分开，则只有最后一个有效
        when(shorthand_list.get(0)).thenReturn(0);
        when(shorthand_list.get(0)).thenReturn(1);
        when(shorthand_list.get(0)).thenReturn(2);
        when(shorthand_list.get(3)).thenReturn(3).thenReturn(4).thenThrow(new RuntimeException());

        assertEquals(2, shorthand_list.get(0));
        assertEquals(2, shorthand_list.get(0));
        assertEquals(3, shorthand_list.get(3));
        assertEquals(4, shorthand_list.get(3));
        //第三次或者更多调用都会抛出异常
        shorthand_list.get(3);
    }

    /**
     * 使用回调生成期望值
     */
    @Test
    public void answer_with_callback() {
        //使用Answer来生成我们期望的返回值
        when(shorthand_list.get(anyInt())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return "hello world:" + args[0];
            }
        });
        assertEquals("hello world:0", shorthand_list.get(0));
        assertEquals("hello world:999", shorthand_list.get(999));
    }

    /**
     * 监控真实对象
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public void spy_real_objects() {
        List list = new LinkedList();
        List spyList = spy(list);
        //下面预设的spyList.get(0)会报错，因为会调用真实对象的get(0), 所以会抛出越界异常
        //when(spyList.get(0)).thenReturn(3);

        //使用doReturn-when 可以避免when-thenReturn 调用真实对象api
        //此处避免了下面调用真实对象了
        doReturn(999).when(spyList).get(999);

        //预设size()的期望值
        when(spyList.size()).thenReturn(100);

        //调用真实对象的api
        spyList.add(1);
        spyList.add(2);
        assertEquals(100, spyList.size());
        assertEquals(1, spyList.get(0));
        assertEquals(2, spyList.get(1));

        verify(spyList).add(1);
        verify(spyList).add(2);
        assertEquals(999, spyList.get(999));
        //测试两个对象是不是一直的
        System.out.println(list == spyList);
        //此处是为了出现IndexOutOfBoundsException
        spyList.get(2);
    }

    /**
     * 修改对未预设的调用返回默认期望值
     */
    @Test
    public void unstubbed_invocations() {
        //mock对象使用Answer来对预设的调用返回默认的期望值
        List list = mock(List.class, new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return 999;
            }
        });
        //下面的get(1)没有预设,通常情况下回返回null，但是使用了Answer改变了默认期望值
        assertEquals(999, list.get(1));
        //下面的size()没有预设,通常情况下回返回0，但是使用了Answer改变了默认期望值
        assertEquals(999, list.size());

    }

    /**
     * 捕捉参数，然后来断言
     */
    @Test
    public void capturing_args() {
        PersonDao personDao = mock(PersonDao.class);
        PersonService ps = new PersonService();
        ps.setPersonDao(personDao);
        //捕捉参数初始化
        ArgumentCaptor<Person> argument = ArgumentCaptor.forClass(Person.class);
        ps.update(1,"jack");
        //捕捉参数
        verify(personDao).update(argument.capture());
        //断言处理
        assertEquals(1, argument.getValue().getId());
        assertEquals("jack", argument.getValue().getName());
    }
    interface PersonDao {
        public void update(Person p);
    }
    class PersonService {
        private PersonDao personDao;

        public PersonDao getPersonDao() {
            return personDao;
        }

        public void setPersonDao(PersonDao personDao) {
            this.personDao = personDao;
        }

        public void update(int id, String name) {
            personDao.update(new Person(id, name));
        }
    }

    class Person {
        private int id;
        private String name;

        public Person(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * 真实部分的mock
     */
    @Test
    public void real_partial_mock() {
        List list = spy(new ArrayList());
        assertEquals(0, list.size());
        A a = mock(A.class);
        //通过thenCallRealMethod来调用真实的api
        when(a.doSomething(anyInt())).thenCallRealMethod();
        assertEquals(999, a.doSomething(999));
    }
    class A {
        public int doSomething(int i) {
            return i;
        }
    }

    @Test
    public void reset_mock() {
        List list = mock(List.class);
        when(list.size()).thenReturn(10);
        list.add(1);
        assertEquals(10, list.size());
        //重置mock，清除所有的互动和预设
        reset(list);
        assertEquals(0, list.size());
    }
}
