package nz.ac.auckland.avatar.services;

import javax.ws.rs.ext.ContextResolver;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import nz.ac.auckland.avatar.domain.Movement;
import nz.ac.auckland.avatar.domain.Curfew;
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
			_context = JAXBContext.newInstance(Avatar.class, Curfew.class,
					Movement.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	@Override
	public JAXBContext getContext(Class<?> type) {
		if (type.equals(Avatar.class) || type.equals(Curfew.class)
				|| type.equals(Movement.class)) {
			return _context;
		} else {
			return null;
		}
	}
}
