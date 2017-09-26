package cn.homjie.kotor;

import cn.homjie.kotor.mq.send.DescriptionSender;
import cn.homjie.kotor.mq.send.TaskInfoSender;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

@MapperScan("cn.homjie.kotor.dao")
@ImportResource("classpath:provider-beans.xml")
@SpringBootApplication
public class KotorApplication {

	private static final String KOTOR_DISTRIBUTED_DESCRIPTION = "kotor.distributed.description";
	private static final String KOTOR_DISTRIBUTED_TASK_INFO = "kotor.distributed.taskInfo";
	private static final String KOTOR_DISTRIBUTED = "kotor.distributed";

	public static void main(String[] args) {
		SpringApplication.run(KotorApplication.class, args);
	}

	@Bean
	public MessageConverter messageConverter() {
		return new SimpleMessageConverter();
	}

	@Bean
	public Queue descriptionQueue() {
		return new Queue(KOTOR_DISTRIBUTED_DESCRIPTION);
	}

	@Bean
	public Queue taskInfoQueue() {
		return new Queue(KOTOR_DISTRIBUTED_TASK_INFO);
	}

	@Bean
	public DirectExchange exchange() {
		return new DirectExchange(KOTOR_DISTRIBUTED);
	}

	@Bean
	public Binding descriptionBind() {
		return BindingBuilder.bind(descriptionQueue()).to(exchange()).with(KOTOR_DISTRIBUTED_DESCRIPTION);
	}

	@Bean
	public Binding taskInfoBind() {
		return BindingBuilder.bind(taskInfoQueue()).to(exchange()).with(KOTOR_DISTRIBUTED_TASK_INFO);
	}

	@Bean
	public RabbitTemplate descriptionTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setExchange(KOTOR_DISTRIBUTED);
		rabbitTemplate.setRoutingKey(KOTOR_DISTRIBUTED_DESCRIPTION);
		return rabbitTemplate;
	}

	@Bean
	public RabbitTemplate taskInfoTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setExchange(KOTOR_DISTRIBUTED);
		rabbitTemplate.setRoutingKey(KOTOR_DISTRIBUTED_TASK_INFO);
		return rabbitTemplate;
	}

	@Bean
	public DescriptionSender descriptionListener(RabbitTemplate descriptionTemplate) {
		return new DescriptionSender(descriptionTemplate);
	}

	@Bean
	public TaskInfoSender taskInfoListener(RabbitTemplate taskInfoTemplate) {
		return new TaskInfoSender(taskInfoTemplate);
	}

	@Bean
	public SimpleMessageListenerContainer listenerDescriptionContainer(ConnectionFactory factory, MessageListener descriptionConsumer) {
		return container(factory, descriptionConsumer, KOTOR_DISTRIBUTED_DESCRIPTION);
	}

	@Bean
	public SimpleMessageListenerContainer listenerTaskInfoContainer(ConnectionFactory factory, MessageListener taskInfoConsumer) {
		return container(factory, taskInfoConsumer, KOTOR_DISTRIBUTED_TASK_INFO);
	}

	private SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListener listener, String... queueName) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(queueName);
		container.setMessageListener(listener);
		return container;
	}
}
