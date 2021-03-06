/*
 * EntityTranslation.java
 *
 * Copyright (C) ECS University of Southampton, 2011
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
 */
package uk.soton.service.mediation.algebra;

import java.util.Hashtable;
import java.util.List;
import java.util.Stack;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.TransformCopy;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpFilter;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprList;
import uk.soton.service.mediation.Alignment;
import uk.soton.service.mediation.EntityTranslationService;

/**
 * The EntityTranslation class extends the Jena ARQ
 * <a href="http://www.openjena.org/ARQ/javadoc/com/hp/hpl/jena/sparql/algebra/TransformCopy.html">TransformCopy</a>
 * in order to visit the ARQ structure of a SPARQL query and translate it according to a provided
 * EntityTranslationService.
 *
 * @author Gianluca Correndo <gc3@ecs.soton.ac.uk>
 */
public class EntityTranslation extends TransformCopy {

    /**
     * The field ets is the instance of the EntityTranslationService used to do the query translation
     *
     * @see uk.soton.service.mediation.EntityTranslationService
     */
    private EntityTranslationService ets;

    /**
     * The field a is the instance of the Alignment used to do the query translation
     */
    private Alignment a;

    /**
     * The field bindingsStack is a Stack of variables bindings
     */
    private Stack<Hashtable<Node, Node>> bindingsStack;

    public EntityTranslation(EntityTranslationService ets, Alignment a) {
        this.ets = ets;
        this.a = a;
        this.bindingsStack = new Stack<Hashtable<Node, Node>>();
    }

    /**
     * Visitor method for OpBGP in order to regenerate the RDF patterns according to the graph rewriting rules
     *
     * @param opBGP the ARQ Basic Graph Pattern operation visited
     * @return the translated ARQ operation
     *
     * @see com.hp.hpl.jena.sparql.algebra.TransformCopy#transform(com.hp.hpl.jena.sparql.algebra.op.OpBGP)
     */
    @Override
    public Op transform(OpBGP opBGP) {
        Op result = new OpBGP(new BasicPattern());
        List<Triple> bpl = (List<Triple>) opBGP.getPattern().getList();
        EntityTranslationService.BGPTranslationResult bgptr = null;
        bgptr = this.ets.getTranslatedTriples(a, bpl);
        for (Triple t : bgptr.getTranslatedBGP()) {
            ((OpBGP) result).getPattern().add(t);
        }
        bindingsStack.push(bgptr.getBindings());
        if (bgptr.getLets().size() > 0) {
            for (OpAssignGenerator assignment : bgptr.getLets()) {
                result = assignment.get(result);
            }
            return result;
        } else {
            return result;
        }
    }

    /**
     * Visitor method for OpFilter in order to rearrange the variables within with the given bindings
     *
     * @param opFilter the ARQ Filter operation visited
     * @return the translated ARQ operation
     * @see com.hp.hpl.jena.sparql.algebra.TransformCopy#transform(com.hp.hpl.jena.sparql.algebra.op.OpFilter,
     * com.hp.hpl.jena.sparql.algebra.Op)
     */
    @Override
    public Op transform(OpFilter opFilter, Op x) {
        ExprList expr = new ExprList();

        for (Expr ex : opFilter.getExprs()) {
            expr.add(ex.copySubstitute(BindingAsHashtable.getBinding(this.bindingsStack.peek())));
        }
        Op opf = OpFilter.filter(expr, x);
        return opf;
    }

}
