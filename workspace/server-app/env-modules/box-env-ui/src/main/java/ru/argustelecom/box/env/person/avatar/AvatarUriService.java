package ru.argustelecom.box.env.person.avatar;

import java.io.InputStream;
import java.sql.SQLException;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ru.argustelecom.box.env.person.avatar.model.PersonAvatar;
import ru.argustelecom.system.inf.exception.SystemException;

@SuppressWarnings("Duplicates")
@Named(value = "avatarUri")
@javax.faces.bean.ApplicationScoped
public class AvatarUriService {

	private static final String PERSON_ID_PARAM_NAME = "personId";
	private static final int DEFAULT_PERSON_ID = -1;

	@Inject
	private PersonAvatarAppService personAvatarAs;

	public StreamedContent getAvatar() throws SQLException {
		FacesContext context = FacesContext.getCurrentInstance();

		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			return new DefaultStreamedContent();
		} else {
			String personIdStr = context.getExternalContext().getRequestParameterMap().get(getPersonIdParamName());
			PersonAvatar personAvatar = personAvatarAs.findAvatar(Long.valueOf(personIdStr));
			if (personAvatar == null) {
				return getDefaultAvatar();
			}

			return new DefaultStreamedContent(personAvatar.getImage().getBinaryStream(), getMimeType(personAvatar));
		}
	}

	public String getPersonIdParamName() {
		return PERSON_ID_PARAM_NAME;
	}

	public int getDefaultPersonId() {
		return DEFAULT_PERSON_ID;
	}

	private StreamedContent getDefaultAvatar() {
		InputStream defaultAvatarIs = this.getClass().getClassLoader()
				.getResourceAsStream("META-INF/resources/resources/box-env/images/default-avatar.png");
		return new DefaultStreamedContent(defaultAvatarIs);
	}

	private String getMimeType(PersonAvatar personAvatar) {
		switch (personAvatar.getFormatName()) {
		case "jpg":
		case "jpeg":
			return "image/jpeg";
		case "png":
			return "image/png";
		default:
			throw new SystemException("Unknown formatName: " + personAvatar.getFormatName());
		}
	}

}