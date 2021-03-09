package dacbench.hyflex.wrapper;

public class ProblemDomainNotFoundException extends IllegalArgumentException {

	/**
	 *
	 */
	private static final long serialVersionUID = 7942973974447131569L;

	public ProblemDomainNotFoundException() {
		super();
	}

	public ProblemDomainNotFoundException(final String s) {
		super(s);
	}

	public ProblemDomainNotFoundException(final Throwable cause) {
		super(cause);
	}

	public ProblemDomainNotFoundException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
