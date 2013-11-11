/*
 * #%L
 * ELK Reasoner
 * *
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.semanticweb.elk.reasoner.datatypes.valuespaces.restricted;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.managers.ElkDatatypeMap;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;
import org.semanticweb.elk.reasoner.datatypes.index.ValueSpaceVisitor;
import org.semanticweb.elk.reasoner.datatypes.numbers.AbstractInterval;
import org.semanticweb.elk.reasoner.datatypes.numbers.BigRational;
import org.semanticweb.elk.reasoner.datatypes.numbers.Endpoint;
import org.semanticweb.elk.reasoner.datatypes.numbers.NegativeInfinity;
import org.semanticweb.elk.reasoner.datatypes.numbers.NumberComparator;
import org.semanticweb.elk.reasoner.datatypes.numbers.PositiveInfinity;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.values.NumericValue;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Representation of numeric value interval with specified restrictions (lower
 * and upper bound)
 *
 * @author Pospishnyi Olexandr
 */
public class NumericIntervalValueSpace extends AbstractInterval implements ValueSpace {

	public ElkDatatype datatype;
	public ElkDatatype effectiveDatatype;
	public Number lowerBound;
	public boolean lowerInclusive;
	public Number upperBound;
	public boolean upperInclusive;

	public NumericIntervalValueSpace(ElkDatatype datatype, Number lowerBound, boolean lowerInclusive, Number upperBound, boolean upperInclusive) {
		this.datatype = datatype;
		this.effectiveDatatype = datatype;
		if (datatype == ElkDatatypeMap.get(PredefinedElkIri.XSD_INTEGER.get()) || datatype == ElkDatatypeMap.get(PredefinedElkIri.XSD_NON_NEGATIVE_INTEGER.get())) {
			if (!lowerInclusive) {
				lowerBound = advance(lowerBound, true);
				lowerInclusive = true;
			}
			if (!upperInclusive) {
				upperBound = advance(upperBound, false);
				upperInclusive = true;
			}
			if (datatype == ElkDatatypeMap.get(PredefinedElkIri.XSD_INTEGER.get())
				&& NumberComparator.INSTANCE.compare(lowerBound, Integer.valueOf(0)) >= 0) {
				effectiveDatatype = ElkDatatypeMap.get(PredefinedElkIri.XSD_NON_NEGATIVE_INTEGER.get());
			}
		}
		this.lowerBound = lowerBound;
		this.lowerInclusive = lowerInclusive;
		this.upperBound = upperBound;
		this.upperInclusive = upperInclusive;
		this.low = new Endpoint(lowerBound, lowerInclusive, true);
		this.high = new Endpoint(upperBound, upperInclusive, false);
	}

	@SuppressWarnings("static-method")
	private Number advance(Number num, boolean increment) {

		if (num instanceof Integer) {
			if (increment) {
				int value = num.intValue();
				if (value == Integer.MAX_VALUE) {
					return ((long) value) + 1;
				} else {
					return value + 1;
				}
			} else {
				int value = num.intValue();
				if (value == Integer.MIN_VALUE) {
					return ((long) value) - 1;
				} else {
					return value - 1;
				}
			}
		} else if (num instanceof Long) {
			if (increment) {
				long value = num.longValue();
				if (value == Long.MAX_VALUE) {
					return BigInteger.valueOf(value).add(BigInteger.ONE);
				} else {
					return value + 1;
				}
			} else {
				long value = num.longValue();
				if (value == Long.MIN_VALUE) {
					return BigInteger.valueOf(value).subtract(BigInteger.ONE);
				} else {
					return value - 1;
				}
			}
		} else if (num instanceof BigInteger) {
			if (increment) {
				return ((BigInteger) num).add(BigInteger.ONE);
			} else {
				return ((BigInteger) num).subtract(BigInteger.ONE);
			}
		} else if (num instanceof BigDecimal) {
			BigDecimal bd = (BigDecimal) num;
			BigInteger bi = bd.toBigInteger();
			if (increment) {
				if (bd.compareTo(BigDecimal.ZERO) > 0) {
					bi = bi.add(BigInteger.ONE);
				}
			} else {
				if (bd.compareTo(BigDecimal.ZERO) < 0) {
					bi = bi.subtract(BigInteger.ONE);
				}
			}
			int biBitCount = bi.bitCount();
			if (biBitCount <= 32) {
				return bi.intValue();
			} else if (biBitCount <= 64) {
				return bi.longValue();
			} else {
				return bi;
			}
		} else if (num instanceof BigRational) {
			BigRational br = (BigRational) num;
			BigDecimal numerator = new BigDecimal(br.getNumerator());
			BigDecimal denominator = new BigDecimal(br.getDenominator());
			BigInteger quotient = numerator.divideToIntegralValue(denominator).toBigInteger();
			if (increment) {
				if (numerator.compareTo(BigDecimal.ZERO) > 0) {
					quotient = quotient.add(BigInteger.ONE);
				}
			} else {
				if (numerator.compareTo(BigDecimal.ZERO) < 0) {
					quotient = quotient.subtract(BigInteger.ONE);
				}
			}
			int quotientBitCount = quotient.bitCount();
			if (quotientBitCount <= 32) {
				return quotient.intValue();
			} else if (quotientBitCount <= 64) {
				return quotient.longValue();
			} else {
				return quotient;
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public boolean isEmpty() {
		int boundComparison = NumberComparator.INSTANCE.compare(lowerBound, upperBound);
		if (boundComparison > 0) {
			return true;
		} else if (boundComparison == 0) {
			if (!lowerInclusive || !upperInclusive
				|| lowerBound == NegativeInfinity.INSTANCE
				|| upperBound == PositiveInfinity.INSTANCE) {
				return true;
			}
			ElkDatatype mostSpecificDatatype = getCorrespondingDatatype(lowerBound);
			return !mostSpecificDatatype.isCompatibleWith(datatype);
		} else {
			return false;
		}
	}

	private ElkDatatype getCorrespondingDatatype(Number number) {
		if (number instanceof Integer || number instanceof Long || number instanceof BigInteger) {
			if (NumberComparator.INSTANCE.compare(number, Integer.valueOf(0)) >= 0) {
				return ElkDatatypeMap.get(PredefinedElkIri.XSD_NON_NEGATIVE_INTEGER.get());
			} else {
				return ElkDatatypeMap.get(PredefinedElkIri.XSD_INTEGER.get());
			}
		} else if (number instanceof BigDecimal) {
			return ElkDatatypeMap.get(PredefinedElkIri.XSD_DECIMAL.get());
		} else if (number instanceof BigRational) {
			return ElkDatatypeMap.get(PredefinedElkIri.OWL_RATIONAL.get());
		} else {
			return ElkDatatypeMap.get(PredefinedElkIri.OWL_REAL.get());
		}
	}

	public boolean isUnipointInterval() {
		return NumberComparator.INSTANCE.compare(lowerBound, upperBound) == 0;
	}

	@Override
	public ElkDatatype getDatatype() {
		return effectiveDatatype;
	}

	@Override
	public ValueSpaceType getType() {
		return ValueSpaceType.NUMERIC_INTERVAL;
	}

	/**
	 * NumericIntervalValueSpace could contain - another
	 * NumericIntervalValueSpace if this value space completely includes
	 * another - NumericValue that is included within specified bounds
	 *
	 * @param valueSpace
	 * @return true if this value space contains {@code valueSpace}
	 */
	@Override
	public boolean contains(ValueSpace valueSpace) {
		switch (valueSpace.getType()) {
			case NUMERIC_VALUE: {
				NumericValue point = (NumericValue) valueSpace;
				if (!point.getDatatype().isCompatibleWith(this.datatype)) {
					return false;
				}
				int l = NumberComparator.INSTANCE.compare(point.value, this.lowerBound);
				int u = NumberComparator.INSTANCE.compare(point.value, this.upperBound);
				return (lowerInclusive ? l >= 0 : l > 0) && (upperInclusive ? u <= 0 : u < 0);
			}
			case NUMERIC_INTERVAL: {
				NumericIntervalValueSpace range = (NumericIntervalValueSpace) valueSpace;
				if (!range.getDatatype().isCompatibleWith(this.datatype)) {
					return false;
				}
				int l = NumberComparator.INSTANCE.compare(this.lowerBound, range.lowerBound);
				int u = NumberComparator.INSTANCE.compare(this.upperBound, range.upperBound);
				boolean result = true;

				if (!this.lowerInclusive && range.lowerInclusive) {
					result &= l < 0;
				} else {
					result &= l <= 0;
				}

				if (!this.upperInclusive && range.upperInclusive) {
					result &= u > 0;
				} else {
					result &= u >= 0;
				}

				return result;
			}
			default:
				return false;
		}
	}

	public boolean contains(Number value, ElkDatatype datatype) {
		if (!datatype.isCompatibleWith(this.datatype)) {
			return false;
		}
		int l = NumberComparator.INSTANCE.compare(value, this.lowerBound);
		int u = NumberComparator.INSTANCE.compare(value, this.upperBound);
		return (lowerInclusive ? l >= 0 : l > 0) && (upperInclusive ? u <= 0 : u < 0);
	}

	@Override
	public boolean isSubsumedBy(ValueSpace valueSpace) {
		return valueSpace.contains(this);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof NumericIntervalValueSpace) {
			NumericIntervalValueSpace otherEntry = (NumericIntervalValueSpace) other;
			return this.datatype.equals(otherEntry.datatype)
				&& this.lowerBound.equals(otherEntry.lowerBound)
				&& this.lowerInclusive == otherEntry.lowerInclusive
				&& this.upperBound.equals(otherEntry.upperBound)
				&& this.upperInclusive == otherEntry.upperInclusive;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return HashGenerator.combinedHashCode(
			NumericIntervalValueSpace.class,
			this.datatype,
			this.lowerBound,
			this.lowerInclusive,
			this.upperBound,
			this.upperInclusive);
	}

	@Override
	public String toString() {
		return (lowerInclusive ? "[" : "(") + lowerBound.toString() + ","
			+ upperBound.toString() + (upperInclusive ? "]" : ")")
			+ "^^" + datatype;
	}

	@Override
	public <O> O accept(ValueSpaceVisitor<O> visitor) {
		return visitor.visit(this);
	}
}
