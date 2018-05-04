/*
 * Copyright 1997-2018 Optimatika
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
package org.ojalgo.optimisation.linear;

import org.junit.jupiter.api.Test;
import org.ojalgo.TestUtils;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Optimisation.Result;
import org.ojalgo.optimisation.linear.LinearSolver.Builder;
import org.ojalgo.optimisation.linear.SimplexTableau.DenseTableau;
import org.ojalgo.optimisation.linear.SimplexTableau.SparseTableau;

/**
 * Examples from LP10-Special-Situations.pdf (Special Situations in the Simplex Algorithm)
 *
 * @author apete
 */
public class SpecialSituations {

    public SpecialSituations() {
        super();
    }

    @Test
    public void testDegeneracy() {

        PrimitiveDenseStore c = PrimitiveDenseStore.FACTORY.columns(new double[] { -2, -1, 0, 0, 0 });
        PrimitiveDenseStore A = PrimitiveDenseStore.FACTORY.rows(new double[][] { { 4, 3, 1, 0, 0 }, { 4, 1, 0, 1, 0 }, { 4, 2, 0, 0, 1 } });
        PrimitiveDenseStore b = PrimitiveDenseStore.FACTORY.columns(new double[] { 12, 8, 8 });

        PrimitiveDenseStore x = PrimitiveDenseStore.FACTORY.columns(new double[] { 2, 0, 4, 0, 0 });

        final Builder builder = LinearSolver.getBuilder(c).equalities(A, b);
        LinearSolver lp = builder.build();

        Result expected = new Optimisation.Result(Optimisation.State.OPTIMAL, x);
        Result actual = lp.solve();

        TestUtils.assertStateAndSolution(expected, actual);

        DenseTableau dense = new SimplexTableau.DenseTableau(builder);
        SparseTableau sparse = new SimplexTableau.SparseTableau(builder);

        TestUtils.assertEquals(dense, sparse);

        SimplexTableau.IterationPoint pivot = new SimplexTableau.IterationPoint();
        pivot.switchToPhase2();

        pivot.row = 1;
        pivot.col = 0;
        dense.pivot(pivot);
        sparse.pivot(pivot);
        TestUtils.assertEquals(dense, sparse);

        pivot.row = 2;
        pivot.col = 1;
        dense.pivot(pivot);
        sparse.pivot(pivot);
        TestUtils.assertEquals(dense, sparse);

        pivot.row = 2;
        pivot.col = 3;
        dense.pivot(pivot);
        sparse.pivot(pivot);
        TestUtils.assertEquals(dense, sparse);

        TestUtils.assertEquals(4.0, dense.doubleValue(3, 5 + 3));
    }

    @Test
    public void testMultipleOptimalSolutions() {

        PrimitiveDenseStore c = PrimitiveDenseStore.FACTORY.columns(new double[] { -4, -14, 0, 0 });
        PrimitiveDenseStore A = PrimitiveDenseStore.FACTORY.rows(new double[][] { { 2, 7, 1, 0 }, { 7, 2, 0, 1 } });
        PrimitiveDenseStore b = PrimitiveDenseStore.FACTORY.columns(new double[] { 21, 21 });

        PrimitiveDenseStore x = PrimitiveDenseStore.FACTORY.columns(new double[] { 7.0 / 3.0, 7.0 / 3.0, 0, 0 });

        final Builder builder = LinearSolver.getBuilder(c).equalities(A, b);
        LinearSolver lp = builder.build();

        Result expected = new Optimisation.Result(Optimisation.State.OPTIMAL, x);
        Result actual = lp.solve();

        TestUtils.assertStateAndSolution(expected, actual);

        DenseTableau dense = new SimplexTableau.DenseTableau(builder);
        SparseTableau sparse = new SimplexTableau.SparseTableau(builder);

        TestUtils.assertEquals(dense, sparse);

        SimplexTableau.IterationPoint pivot = new SimplexTableau.IterationPoint();
        pivot.switchToPhase2();

        pivot.row = 0;
        pivot.col = 1;
        dense.pivot(pivot);
        sparse.pivot(pivot);
        TestUtils.assertEquals(dense, sparse);

        pivot.row = 1;
        pivot.col = 0;
        dense.pivot(pivot);
        sparse.pivot(pivot);
        TestUtils.assertEquals(dense, sparse);

        TestUtils.assertEquals(42.0, dense.doubleValue(2, 4 + 2));
    }

    @Test
    public void testUnboundedness() {

        PrimitiveDenseStore c = PrimitiveDenseStore.FACTORY.columns(new double[] { -2, -1, 0, 0 });
        PrimitiveDenseStore A = PrimitiveDenseStore.FACTORY.rows(new double[][] { { 1, -1, 1, 0 }, { 2, -1, 0, 1 } });
        PrimitiveDenseStore b = PrimitiveDenseStore.FACTORY.columns(new double[] { 10, 40 });

        PrimitiveDenseStore x = PrimitiveDenseStore.FACTORY.columns(new double[] { 30, 20, 0, 0 });

        final Builder builder = LinearSolver.getBuilder(c).equalities(A, b);
        LinearSolver lp = builder.build();

        Result expected = new Optimisation.Result(Optimisation.State.UNBOUNDED, x);
        Result actual = lp.solve();

        TestUtils.assertStateAndSolution(expected, actual);

        DenseTableau dense = new SimplexTableau.DenseTableau(builder);
        SparseTableau sparse = new SimplexTableau.SparseTableau(builder);

        TestUtils.assertEquals(dense, sparse);

        SimplexTableau.IterationPoint pivot = new SimplexTableau.IterationPoint();
        pivot.switchToPhase2();

        pivot.row = 0;
        pivot.col = 0;
        dense.pivot(pivot);
        sparse.pivot(pivot);
        TestUtils.assertEquals(dense, sparse);

        pivot.row = 1;
        pivot.col = 1;
        dense.pivot(pivot);
        sparse.pivot(pivot);
        TestUtils.assertEquals(dense, sparse);

        TestUtils.assertEquals(80.0, dense.doubleValue(2, 4 + 2));
    }

}
