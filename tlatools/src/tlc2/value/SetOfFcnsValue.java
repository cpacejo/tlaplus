// Copyright (c) 2003 Compaq Corporation.  All rights reserved.
// Portions Copyright (c) 2003 Microsoft Corporation.  All rights reserved.
// Last modified on Wed 12 Jul 2017 at 16:10:00 PST by ian morris nieves
//      modified on Sat 23 February 2008 at 10:17:11 PST by lamport
//      modified on Fri Aug 10 15:09:46 PDT 2001 by yuanyu

package tlc2.value;

import java.io.IOException;
import java.math.BigInteger;

import tlc2.TLCGlobals;
import tlc2.tool.FingerprintException;
import util.Assert;

public class SetOfFcnsValue extends SetOfFcnsOrRcdsValue implements Enumerable {
  public final Value domain;        /* Function domain  */
  public final Value range;         /* Function range   */
  protected SetEnumValue fcnSet;

  /* Constructor */
  public SetOfFcnsValue(Value domain, Value range) {
    this.domain = domain;
    this.range = range;
    this.fcnSet = null;
  }

  public final byte getKind() { return SETOFFCNSVALUE; }

  public final int compareTo(Object obj) {
    try {
      this.convertAndCache();
      return this.fcnSet.compareTo(obj);
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final boolean equals(Object obj) {
    try {
      if (obj instanceof SetOfFcnsValue) {
        SetOfFcnsValue fcns = (SetOfFcnsValue)obj;
        return (this.domain.equals(fcns.domain) &&
          this.range.equals(fcns.range));
      }
      this.convertAndCache();
      return this.fcnSet.equals(obj);
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final boolean member(Value elem) {
    try {
      FcnRcdValue fcn = elem.toFcnRcd();
      if (fcn == null) {
        if (elem instanceof ModelValue)
           return ((ModelValue) elem).modelValueMember(this) ;
        Assert.fail("Attempted to check if \n" + elem + "\nwhich is not a TLC function" +
        " value, is in the set of functions:\n" + ppr(this.toString()));
      }
      if (fcn.intv == null) {
        fcn.normalize();
        Value fdom = new SetEnumValue(fcn.domain, true);
        if (this.domain.equals(fdom)) {
          for (int i = 0; i < fcn.values.length; i++) {
            if (!this.range.member(fcn.values[i])) {
              return false;
            }
          }
          return true;
        }
      }
      else {
        if (fcn.intv.equals(this.domain)) {
          for (int i = 0; i < fcn.values.length; i++) {
            if (!this.range.member(fcn.values[i])) return false;
          }
          return true;
        }
      }
      return false;
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final boolean isFinite() {
    try {
      return this.domain.isFinite() && this.range.isFinite();
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final Value takeExcept(ValueExcept ex) {
    try {
      if (ex.idx < ex.path.length) {
        Assert.fail("Attempted to apply EXCEPT to the set of functions:\n" +
        ppr(this.toString()));
      }
      return ex.value;
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final Value takeExcept(ValueExcept[] exs) {
    try {
      if (exs.length != 0) {
        Assert.fail("Attempted to apply EXCEPT to the set of functions:\n" +
        ppr(this.toString()));
      }
      return this;
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final int size() {
    try {
      int dsz = this.domain.size();
      int rsz = this.range.size();
      long sz = 1;
      for (int i = 0; i < dsz; i++) {
        sz *= rsz;
        if (sz < -2147483648 || sz > 2147483647) {
          Assert.fail("Overflow when computing the number of elements in:\n" +
                ppr(toString()));
        }
      }
      return (int)sz;
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

	@Override
	protected boolean needBigInteger() {
		final int rsz = this.range.size();
		final int dsz = this.domain.size();
		long sz = 1;
		for (int i = 0; i < dsz; i++) {
			sz *= rsz;
			if (sz < -2147483648 || sz > 2147483647) {
				return true;
			}
		}
		return false;
	}

  public final boolean isNormalized() {
    try {
      if (this.fcnSet == null || this.fcnSet == DummyEnum) {
        return this.domain.isNormalized() && this.range.isNormalized();
      }
      return this.fcnSet.isNormalized();
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final Value normalize() {
    try {
      if (this.fcnSet == null || this.fcnSet == DummyEnum) {
        this.domain.normalize();
        this.range.normalize();
      }
      else {
        this.fcnSet.normalize();
      }
      return this;
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final boolean isDefined() {
    try {
      return this.domain.isDefined() && this.range.isDefined();
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final Value deepCopy() { return this; }

  public final boolean assignable(Value val) {
    try {
      return this.equals(val);
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  /* The fingerprint  */
  public final long fingerPrint(long fp) {
    try {
      this.convertAndCache();
      return this.fcnSet.fingerPrint(fp);
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final Value permute(MVPerm perm) {
    try {
      this.convertAndCache();
      return this.fcnSet.permute(perm);
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  private final void convertAndCache() {
    if (this.fcnSet == null) {
      this.fcnSet = this.toSetEnum();
    }
    else if (this.fcnSet == DummyEnum) {
      SetEnumValue val = null;
      synchronized(this) {
        if (this.fcnSet == DummyEnum) {
          val = this.toSetEnum();
          val.deepNormalize();
        }
      }
      synchronized(this) {
        if (this.fcnSet == DummyEnum) { this.fcnSet = val; }
      }
    }
  }

  @Override
  public final SetEnumValue toSetEnum() {
      if (this.fcnSet != null && this.fcnSet != DummyEnum) {
        return this.fcnSet;
      }
      ValueVec vals = new ValueVec();
      ValueEnumeration Enum = this.elements();
      Value elem;
      while ((elem = Enum.nextElement()) != null) {
        vals.addElement(elem);
      }
      return new SetEnumValue(vals, this.isNormalized());
  }

  @Override
  public final void write(final ValueOutputStream vos) throws IOException {
	  fcnSet.write(vos);
  }

  /* The string representation of the value. */
  public final StringBuffer toString(StringBuffer sb, int offset) {
    try {
      boolean unlazy = expand;
      try {
        if (unlazy) {
          int dsz = this.domain.size();
          int rsz = this.range.size();
          long sz = 1;
          for (int i = 0; i < dsz; i++) {
            sz *= rsz;
            if (sz < -2147483648 || sz > 2147483647) {
              unlazy = false;
              break;
            }
          }
          unlazy = sz < TLCGlobals.enumBound;
        }
      }
      catch (Throwable e) { unlazy = false; }

      if (unlazy) {
        Value val = this.toSetEnum();
        return val.toString(sb, offset);
      }
      else {
        sb.append("[");
        this.domain.toString(sb, offset);
        sb.append(" -> ");
        this.range.toString(sb, offset);
        sb.append("]");
        return sb;
      }
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final ValueEnumeration elements() {
    try {
      if (this.fcnSet == null || this.fcnSet == DummyEnum) {
        return new Enumerator();
      }
      return this.fcnSet.elements();
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  final class Enumerator implements ValueEnumeration {
    private Value[] dom;
    private ValueEnumeration[] enums;
    private Value[] currentElems;
    private boolean isDone;

    public Enumerator() {
      this.isDone = false;
      SetEnumValue domSet = domain.toSetEnum();
      if (domSet == null)
        Assert.fail("Attempted to enumerate a set of the form [D -> R]," +
              "but the domain D:\n" + ppr(domain.toString()) +
              "\ncannot be enumerated.");
      domSet.normalize();
      ValueVec elems = domSet.elems;
      int sz = elems.size();
      if (range instanceof Enumerable) {
        this.dom = new Value[sz];
        this.enums = new ValueEnumeration[sz];
        this.currentElems = new Value[sz];
        // SZ Feb 24, 2009: never read locally
        // ValueEnumeration enumeration = ((Enumerable)domSet).elements();
        for (int i = 0; i < sz; i++) {
          this.dom[i] = elems.elementAt(i);
          this.enums[i] = ((Enumerable)range).elements();
          this.currentElems[i] = this.enums[i].nextElement();
          if (this.currentElems[i] == null) {
            this.enums = null;
            this.isDone = true;
            break;
          }
        }
      }
      else {
        Assert.fail("Attempted to enumerate a set of the form [D -> R]," +
              "but the range R:\n" + ppr(range.toString()) +
              "\ncannot be enumerated.");
      }
    }

    public final void reset() {
      if (this.enums != null) {
        for (int i = 0; i < this.enums.length; i++) {
          this.enums[i].reset();
          this.currentElems[i] = this.enums[i].nextElement();
        }
        this.isDone = false;
      }
    }

		public final Value nextElement() {
			if (this.isDone) {
				return null;
			}
			if (this.currentElems.length == 0) {
				this.isDone = true;
				return new FcnRcdValue(this.dom, new Value[this.currentElems.length], true);
			} else {
				// Take and store a snapshot of currentElems as the element to return for
				// this invocation of nextElement().
				final Value[] elems = new Value[this.currentElems.length];
				System.arraycopy(this.currentElems, 0, elems, 0, this.currentElems.length);

				// Eagerly generate the next element which is going to be returned the upon next
				// invocation of nextElement().
				for (int i = this.currentElems.length - 1; i >= 0; i--) {
					this.currentElems[i] = this.enums[i].nextElement();
					if (this.currentElems[i] != null) {
						break;
					}
					if (i == 0) {
						this.isDone = true;
						break;
					}
					this.enums[i].reset();
					this.currentElems[i] = this.enums[i].nextElement();
				}
				
				return new FcnRcdValue(this.dom, elems, true);
			}
		}

  }
	
	@Override
	protected tlc2.value.SetOfFcnsOrRcdsValue.SubsetEnumerator getSubsetEnumerator(int k, int n) {
		return new SubsetEnumerator(k, n);
	}

	class SubsetEnumerator extends SetOfFcnsOrRcdsValue.SubsetEnumerator {
		private final SetEnumValue domSet;
		private final SetEnumValue rangeSet;
		private final int mod;
		
		SubsetEnumerator(final int k, final int n) {
			super(k, n);
			domSet = domain.toSetEnum();
			domSet.normalize();

			rangeSet = range.toSetEnum();

			mod = range.size();
		}

		@Override
		protected Value elementAt(final int idx) {
			assert 0 <= idx && idx < size();

			final Value[] range = new Value[domSet.size()];

			for (int i = 0; i < domSet.size(); i++) {
				final int elementAt = (int) (Math.floor(idx / Math.pow(mod, i)) % mod);
				range[range.length - 1 - i] = rangeSet.elems.elementAt(elementAt);
			}

			return new FcnRcdValue(domSet.elems, range, true);
		}
	}

	@Override
	protected tlc2.value.SetOfFcnsOrRcdsValue.BigIntegerSubsetEnumerator getBigSubsetEnumerator(int k) {
		return new BigIntegerSubsetEnumerator(k);
	}
	
	class BigIntegerSubsetEnumerator extends SetOfFcnsOrRcdsValue.BigIntegerSubsetEnumerator {
		
		private final SetEnumValue domSet;
		private final SetEnumValue rangeSet;
		private final BigInteger bMod;
		private final int mod;

		public BigIntegerSubsetEnumerator(final int k) {
			super(k);
			this.domSet = domain.toSetEnum();
			this.domSet.normalize();
			
			this.rangeSet = range.toSetEnum();
			this.mod = range.size();
			this.bMod = BigInteger.valueOf(mod);

			this.sz = bMod.pow(domSet.size());
		}

		@Override
		protected Value elementAt(final BigInteger idx) {
			final Value[] range = new Value[domSet.size()];

			for (int i = 0; i < domSet.size(); i++) {
				final long scale = (long) Math.pow(mod, i);
				final BigInteger bScale = BigInteger.valueOf(scale);
				// idx2 is the index in the range (0,range.size^domset.size] 
				final BigInteger idx2 = idx.divide(bScale);
				final int elementAt = idx2.mod(bMod).intValueExact();
				range[range.length - 1 - i] = rangeSet.elems.elementAt(elementAt);
			}

			return new FcnRcdValue(domSet.elems, range, true);
		}
	}
}
