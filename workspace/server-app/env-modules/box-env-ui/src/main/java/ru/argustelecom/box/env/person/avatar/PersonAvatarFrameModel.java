package ru.argustelecom.box.env.person.avatar;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import ru.argustelecom.box.env.person.PersonDataDto;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "personAvatarFM")
@PresentationModel
public class PersonAvatarFrameModel implements Serializable {

	private static final long serialVersionUID = -7831933777429197793L;

	@Inject
	private PersonAvatarAppService personAvatarAppService;

	private PersonDataDto personDataDto;

	private boolean avatarChanged;
	private byte[] uploadedAvatar;

	public void preRender(PersonDataDto personDataDto) {
		this.personDataDto = personDataDto;
	}

	public boolean canRemoveAvatar() {
		return avatarChanged || personDataDto.getImageInputStream() != null;
	}

	public void removeAvatar() {
		personDataDto.setImageInputStream(null);
		personDataDto.setImageFormatName(null);
		avatarChanged = true;
		uploadedAvatar = null;
	}

	public void clean() {
		avatarChanged = false;
		uploadedAvatar = null;
	}

	public boolean isAvatarChanged() {
		return avatarChanged;
	}

	public boolean showUploadedAvatar() {
		return isAvatarChanged() && uploadedAvatar != null;
	}

	public boolean showDefaultAvatar() {
		return !(personDataDto.getPersonId() != null && personDataDto.getImageInputStream() != null);
	}

	public byte[] getUploadedAvatar() {
		return uploadedAvatar;
	}

}