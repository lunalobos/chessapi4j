package chessapi4j;

/**
 * @author lunalobos
 * 
 * @since 1.2.9
 */
final class CheckInfo {
	private long inCheck;
	private long inCheckMask;
	private int checkCount;

	public CheckInfo(long inCheck, long inCheckMask, int checkCount) {
		super();
		this.inCheck = inCheck;
		this.inCheckMask = inCheckMask;
		this.checkCount = checkCount;
	}

	public long getInCheck() {
		return inCheck;
	}

	public void setInCheck(long inCheck) {
		this.inCheck = inCheck;
	}

	public long getInCheckMask() {
		return inCheckMask;
	}

	public void setInCheckMask(long inCheckMask) {
		this.inCheckMask = inCheckMask;
	}

	public int getCheckCount() {
		return checkCount;
	}

	public void setCheckCount(int checkCount) {
		this.checkCount = checkCount;
	}

}
