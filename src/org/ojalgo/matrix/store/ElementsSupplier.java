/*
 * Copyright 1997-2016 Optimatika (www.optimatika.se)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.ojalgo.matrix.store;

import java.util.function.Supplier;

import org.ojalgo.access.Factory2D;
import org.ojalgo.access.Stream2D;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.UnaryFunction;

/**
 * An elements supplier is not (yet) a matrix, but there are several matrix related things you can do with
 * them:
 * <ol>
 * <li>You can query the size/shape of the (future) matrix.</li>
 * <li>You can supply the elements to an already existing matrix (or more precisely to an
 * {@linkplain ElementsConsumer}) or collect them using a {@linkplain Factory2D}.</li>
 * <li>You can define a stream of additional operations to be executed when the elements are extracted.</li>
 * <li>You can get that matrix</li>
 * </ol>
 *
 * @author apete
 */
public interface ElementsSupplier<N extends Number> extends Stream2D<N, MatrixStore<N>, ElementsConsumer<N>, ElementsSupplier<N>>, Supplier<MatrixStore<N>> {

    default MatrixStore<N> get() {
        return this.collect(this.physical());
    }

    default ElementsSupplier<N> operateOnAll(final UnaryFunction<N> operator) {

        return new MatrixPipeline<N>(this) {

            public long count() {
                return ElementsSupplier.this.count();
            }

            public long countColumns() {
                return ElementsSupplier.this.countColumns();
            }

            public long countRows() {
                return ElementsSupplier.this.countRows();
            }

            @Override
            public void supplyTo(final ElementsConsumer<N> receiver) {
                ElementsSupplier.this.supplyTo(receiver);
                receiver.modifyAll(operator);
            }

        };

    }

    default ElementsSupplier<N> operateOnMatching(final BinaryFunction<N> operator, final MatrixStore<N> right) {

        return new MatrixPipeline<N>(this) {

            public long count() {
                return ElementsSupplier.this.count();
            }

            public long countColumns() {
                return ElementsSupplier.this.countColumns();
            }

            public long countRows() {
                return ElementsSupplier.this.countRows();
            }

            @Override
            public void supplyTo(final ElementsConsumer<N> receiver) {
                ElementsSupplier.this.supplyTo(receiver);
                receiver.modifyMatching(operator, right);
            }

        };

    }

    default ElementsSupplier<N> operateOnMatching(final MatrixStore<N> left, final BinaryFunction<N> operator) {

        return new MatrixPipeline<N>(this) {

            public long count() {
                return ElementsSupplier.this.count();
            }

            public long countColumns() {
                return ElementsSupplier.this.countColumns();
            }

            public long countRows() {
                return ElementsSupplier.this.countRows();
            }

            @Override
            public void supplyTo(final ElementsConsumer<N> receiver) {
                ElementsSupplier.this.supplyTo(receiver);
                receiver.modifyMatching(left, operator);
            }

        };

    }

    PhysicalStore.Factory<N, ?> physical();

    default ElementsSupplier<N> transpose() {

        return new MatrixPipeline<N>(this) {

            public long count() {
                return ElementsSupplier.this.count();
            }

            public long countColumns() {
                return ElementsSupplier.this.countRows();
            }

            public long countRows() {
                return ElementsSupplier.this.countColumns();
            }

            public MatrixStore<N> get() {

                final PhysicalStore<N> retVal = this.physical().makeZero(ElementsSupplier.this.countRows(), ElementsSupplier.this.countColumns());

                this.supplyTo(retVal);

                return retVal;
            }

            public ElementsSupplier<N> operateOnAll(final UnaryFunction<N> operator) {
                return ElementsSupplier.this.operateOnAll(operator);
            }

            public ElementsSupplier<N> operateOnMatching(final BinaryFunction<N> operator, final MatrixStore<N> right) {
                return ElementsSupplier.this.operateOnMatching(operator, right.transpose());
            }

            public ElementsSupplier<N> operateOnMatching(final MatrixStore<N> left, final BinaryFunction<N> operator) {
                return ElementsSupplier.this.operateOnMatching(left.transpose(), operator);
            }

            @Override
            public void supplyTo(final ElementsConsumer<N> receiver) {
                ElementsSupplier.this.supplyTo(receiver.regionByTransposing());
            }

            public ElementsSupplier<N> transpose() {
                return ElementsSupplier.this;
            }

        };

    }

}
