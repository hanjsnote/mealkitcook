package com.mealkitcook.entity;

import com.mealkitcook.constant.ItemSellStatus;
import com.mealkitcook.repository.ItemRepository;
import com.mealkitcook.repository.MemberRepository;
import com.mealkitcook.repository.OrderItemRepository;
import com.mealkitcook.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class OrderTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @PersistenceContext
    EntityManager em;

    public Item createItem(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("상세설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        return item;
    }

    @Test
    @DisplayName("영속성 전이 테스트")
    public void cascadeTest(){

        Order order = new Order();

        for(int i=0; i<3; i++){
            Item item = this.createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);   //아직 영속성 컨텍스트에 저장되지 않은 orderItem엔티티를 order 엔티티에 담아준다
        }

        orderRepository.saveAndFlush(order);    //order엔티티를 저장하면서 강제로 flush를 호출하여 영속성 컨텍스트에 있는 객체들을 DB에 반영한다
        em.clear(); //영속성 컨텍스트의 상태를 초기화

        Order savedOrder = orderRepository.findById(order.getId())  //영속성 컨텍스트를 초기화했기 때문에 DB에서 주문엔티티를 조회한다
                .orElseThrow(EntityNotFoundException::new);
        assertEquals(3, savedOrder.getOrderItems().size());
    }

    public Order createOrder(){ //주문데이터를 생성해서 저장하는 메소드
        Order order = new Order();

        for(int i=0; i<3; i++){
            Item item = createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }

        Member member = new Member();
        memberRepository.save(member);

        order.setMember(member);
        orderRepository.save(order);
        return order;
    }

    @Test
    @DisplayName("고아객체 제거 테스트")
    public void orphanRemovalTest(){
        Order order = this.createOrder();
        order.getOrderItems().remove(0);    //order엔티티에서 관리하고 있는 orderItem리스트의 0번째 인덱스 요소를 제거합니다
        em.flush();
    }

    /*@Test
    @DisplayName("지연 로딩 테스트")
    public void lazyLoadingTest(){
        Order order = this.createOrder();   //기존에 만들었던 주문 생성 메소드를 이용하여 주문데이터를 저장
        Long orderItemId = order.getOrderItems().get(0).getId();
        em.flush();
        em.clear();

        OrderItem orderItem = orderItemRepository.findById(orderItemId) //영속성 컨텍스트의 상태 초기화후 order엔티티에 저장했던 주문 상품 아이디를 이용하여 orderItem을 db에서 다시 조회한다
                .orElseThrow(EntityNotFoundException::new);
        System.out.println("Order class : " +
                            orderItem.getOrder().getClass());   //orderItem엔티티에 있는 order객체의 클래스를 출력, Order클래스가 출력되는것을 확인할수있다
    }*/

    @Test
    @DisplayName("지연 로딩 테스트")
    public void lazyLoadingTest(){
        Order order = this.createOrder();   //기존에 만들었던 주문 생성 메소드를 이용하여 주문데이터를 저장
        Long orderItemId = order.getOrderItems().get(0).getId();
        em.flush();
        em.clear();

        OrderItem orderItem = orderItemRepository.findById(orderItemId) //영속성 컨텍스트의 상태 초기화후 order엔티티에 저장했던 주문 상품 아이디를 이용하여 orderItem을 db에서 다시 조회한다
                .orElseThrow(EntityNotFoundException::new);
        System.out.println("Order class : " + orderItem.getOrder().getClass());
        System.out.println("========================");
        orderItem.getOrder().getOrderDate();
        System.out.println("========================");
    }

}