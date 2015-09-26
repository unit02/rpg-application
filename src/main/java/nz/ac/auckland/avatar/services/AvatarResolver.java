package nz.ac.auckland.avatar.services;

import javax.ws.rs.ext.ContextResolver;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import nz.ac.auckland.avatar.domain.Quest;
import nz.ac.auckland.avatar.domain.Achievement;
import nz.ac.auckland.avatar.domain.Avatar;

/**
 * ContextResolver implementation to return a customised JAXBContext for the
 * Avatar Web service.
 * 
 * @author Ian Warren
 *
 */
public class AvatarResolver implements ContextResolver<JAXBContext> {
	private JAXBContext _context;

	public AvatarResolver() {
		try {
			// The JAXV Context should be able to marshal and unmarshal the
			// specified classes.
			_context = JAXBContext.newInstance(Avatar.class, Achievement.class,
					Quest.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	@Override
	public JAXBContext getContext(Class<?> type) {
		if (type.equals(Avatar.class) || type.equals(Achievement.class)
				|| type.equals(Quest.class)) {
			return _context;
		} else {
			return null;
		}
	}
}
