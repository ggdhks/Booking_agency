
package org.jboss.quickstarts.wfk.contact;

import javax.validation.ValidationException;

/**
 * <p>ValidationException caused if a Contact's email address conflicts with that of another Contact.</p>
 *
 * <p>This violates the uniqueness constraint.</p>
 *
 * @author hugofirth
 * @see Contact
 */
public class UniqueEmailException extends ValidationException {

	private static final long serialVersionUID = 1L;

	public UniqueEmailException(String message) {
        super(message);
    }

    public UniqueEmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public UniqueEmailException(Throwable cause) {
        super(cause);
    }
}
