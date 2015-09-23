package nz.ac.auckland.parolee.services;

import javax.ws.rs.ext.ContextResolver;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import nz.ac.auckland.parolee.domain.Curfew;
import nz.ac.auckland.parolee.domain.Movement;
import nz.ac.auckland.parolee.domain.Parolee;

/**
 * ContextResolver implementation to return a customised JAXBContext for the
 * Parolee Web service.
 * 
 * @author Ian Warren
 *
 */
public class ParoleeResolver implements ContextResolver<JAXBContext> {
	private JAXBContext _context;

	public ParoleeResolver() {
		try {
			// The JAXV Context should be able to marshal and unmarshal the
			// specified classes.
			_context = JAXBContext.newInstance(Parolee.class, Curfew.class,
					Movement.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	@Override
	public JAXBContext getContext(Class<?> type) {
		if (type.equals(Parolee.class) || type.equals(Curfew.class)
				|| type.equals(Movement.class)) {
			return _context;
		} else {
			return null;
		}
	}
}
