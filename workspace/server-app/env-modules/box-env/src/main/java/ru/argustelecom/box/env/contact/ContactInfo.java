package ru.argustelecom.box.env.contact;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import ru.argustelecom.system.inf.exception.SystemException;

/**
 * Класс являющийся <b>Value Object</b>, который хранит в себе информацию о всех контактах, некоторого объекта. Умеет
 * выполнять операции с документами: добавлять, удалять.
 * 
 * <p>
 * Для того, чтобы у объекта появился список контактов необходимо включить в соответствующий класс <b>ContactInfo</b> и
 * сделать переопределение аннотации на таблицу развязки, аналогично тому, как это сделано для
 * {@linkplain ru.argustelecom.box.env.party.model.Party#contactInfo контактов участника}:
 * 
 * <code><pre>
 * AssociationOverride(
 *     name = "contacts", 
 *     joinTable = @JoinTable(
 *         name = "party_contacts",
 *         joinColumns = @JoinColumn(name = "party_id", referencedColumnName = "id"),
 *         inverseJoinColumns = @JoinColumn(name = "contact_id", referencedColumnName = "id")
 *     )
 * )</pre></code>
 *
 */
@Embeddable
@Access(AccessType.FIELD)
public class ContactInfo implements Serializable {

	@OneToMany(targetEntity = Contact.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "contact_id")
	private List<Contact<?>> contacts = new ArrayList<>();

	public ContactInfo() {
	}

	/**
	 * @return Неизменямый список контактов.
	 */
	public List<Contact<?>> getContacts() {
		return Collections.unmodifiableList(contacts);
	}

	/**
	 * Находит контакт по его идентификатору. Если контакта с таким идентификатором нет в текущем контексте контактов,
	 * то вернет null
	 * 
	 * @param contactId
	 *            - идентификатор контакта
	 * 
	 * @return контакт или <tt>null</tt>, если контакта нет в текущем контексте
	 */
	public Contact<?> getContact(long contactId) {
		for (Contact<?> contact : contacts) {
			if (contact.getId().longValue() == contactId) {
				return contact;
			}
		}
		return null;
	}

	/**
	 * Находит контакт по его идентификатору и приводит его тип к указанному. Если контакта с таким идентификатором нет
	 * в текущем контексте контактов, то вернет null. Если контакт с указанным идентификатором есть, но его тип
	 * отличается от требуемого, то будет возвращен <tt>null</tt>.
	 * 
	 * @param contactClass
	 *            - класс контакта. Не может быть <tt>null</tt>
	 * @param contactId
	 *            - идентификатор контакта
	 * 
	 * @return контакт или <tt>null</tt>, если контакта нет в текущем контексте
	 */
	public <T extends Contact<?>> T getContact(Class<T> contactClass, long contactId) {
		checkArgument(contactClass != null);

		Contact<?> result = getContact(contactId);
		if (result != null && contactClass.isAssignableFrom(result.getClass())) {
			return contactClass.cast(result);
		}
		return null;
	}

	/**
	 * Проверяет, что указанный контакт соответствует указанному классу контакта и выполняет приведение типа контакта.
	 * 
	 * @param contactClass
	 *            - класс, к которому нужно привести указанный контакт. Не может быть <tt>null</tt>
	 * @param contact
	 *            - контакт, который нужно привести к указанному типу. Не может быть <tt>null</tt>
	 * 
	 * @return
	 */
	public <T extends Contact<?>> T castContact(Class<T> contactClass, Contact<?> contact) {
		checkArgument(contact != null);
		checkArgument(contactClass != null);
		return contactClass.isAssignableFrom(contact.getClass()) ? contactClass.cast(contact) : null;
	}

	/**
	 * Создаёт контакт указанного типа и добавляет его в список контактов. По указанному типу контакта будет определена
	 * его категория, с которой ассоциирован класс контакта. Для класса контакта определен тип значения контакта,
	 * например, для PhoneContact это будет PhoneNumber. Соответственно, в этом методе перед непосредственным созданием
	 * контакта выполняется проверка, что указанное значение контакта совместимо с определенным на уровне Java класса.
	 * Если это условие не будет выполнено, то будет спровоцировано соответствующее исключение.
	 * 
	 * @param contactType
	 *            - тип создаваемого контакта. Не может быть <tt>null</tt>
	 * @param value
	 *            - значение создаваемого контакта. Должно соответствовать типу контакта. Например, для PhoneContact
	 *            нужно указать PhoneNumber, а не произвольную строку. Не может быть <tt>null</tt>
	 * @param id
	 *            - индетификатор создаваемого контакта. Должен быть корректно определен.
	 * 
	 * @return созданный контакт.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Contact<?> createContact(ContactType contactType, Object value, Long id) {
		checkArgument(contactType != null);
		checkArgument(value != null);
		checkArgument(contactType.getCategory().isAssignableFrom(value));

		Class contactClass = contactType.getCategory().getContactClass();
		Contact<?> newContact = createContact(contactClass, value, id);
		newContact.setType(contactType);
		return newContact;
	}

	/**
	 * Создаёт контакт указанного типа и добавляет его в список контактов. По указанному типу контакта будет определена
	 * его категория, с которой ассоциирован класс контакта. Класс контакта, указанный в категории должен быть
	 * эквивалентен указанному в качестве аргумента этого метода.
	 * 
	 * @param contactClass
	 *            - класс создаваемого контакта. Не может быть <tt>null</tt>
	 * @param contactType
	 *            - тип создаваемого контакта, должен иметь категорию с классом, эквивалентным указанному в качестве
	 *            аргумента. Не может быть <tt>null</tt>
	 * @param value
	 *            - значение создаваемого контакта. Должно соответствовать типу контакта. Например, для PhoneContact
	 *            нужно указать PhoneNumber, а не произвольную строку. Не может быть <tt>null</tt>
	 * @param id
	 *            - индетификатор создаваемого контакта. Должен быть корректно определен.
	 * 
	 * @return созданный контакт.
	 */
	public <V, T extends Contact<V>> T createContact(Class<T> contactClass, ContactType contactType, V value, Long id) {
		checkArgument(contactClass != null);
		checkArgument(contactType != null);
		checkArgument(value != null);
		checkArgument(Objects.equals(contactClass, contactType.getCategory().getContactClass()));

		T newContact = doCreateContact(contactClass, value, id);
		newContact.setType(contactType);
		return newContact;
	}

	/**
	 * Удаляет указанный контакт. Не делает никаких дополнительных проверок, удаление происходит безусловно. Если
	 * удалось удалить (контак действительно был в текущем контектсте контактов), то вернет <tt>true</tt>
	 * 
	 * @param contact
	 *            удаляемый контакт. Не может быть <tt>null</tt>
	 */
	public boolean remove(Contact<?> contact) {
		checkArgument(contact != null);
		return contacts.remove(contact);
	}

	/**
	 * Нужен для раскрытия параметризованного типа V и преобразования к нему Object value. По факту - чит. Безопасность
	 * гарантируется обязательной проверкой предварительного условия в {@link #createContact(ContactType, Object, Long)}
	 * 
	 * @param <V>
	 *            тип значения контакта
	 * @param <T>
	 *            тип контакта
	 */
	private <V, T extends Contact<V>> T createContact(Class<T> clazz, Object value, Long id) {
		@SuppressWarnings("unchecked")
		V typedValue = (V) value;
		return doCreateContact(clazz, typedValue, id);
	}

	/**
	 * Непосредственно создает контакт указанного класса и присваивает ему указанное значение.
	 */
	private <V, T extends Contact<V>> T doCreateContact(Class<T> clazz, V value, Long id) {
		T newContact = null;
		try {
			newContact = clazz.getDeclaredConstructor(Long.class).newInstance(id);
		} catch (Exception e) {
			throw new SystemException("Unable to instantiate Contact instance", e);
		}

		newContact.setValue(value);
		contacts.add(newContact);
		return newContact;
	}

	private static final long serialVersionUID = -2186323902734138106L;
}