/*
 * PropertyVisitor.java
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
package uk.soton.service.mediation.edoal;

import fr.inrialpes.exmo.align.impl.edoal.Aggregate;
import fr.inrialpes.exmo.align.impl.edoal.Apply;
import fr.inrialpes.exmo.align.impl.edoal.ClassConstruction;
import fr.inrialpes.exmo.align.impl.edoal.ClassDomainRestriction;
import fr.inrialpes.exmo.align.impl.edoal.ClassExpression;
import fr.inrialpes.exmo.align.impl.edoal.ClassId;
import fr.inrialpes.exmo.align.impl.edoal.ClassOccurenceRestriction;
import fr.inrialpes.exmo.align.impl.edoal.ClassRestriction;
import fr.inrialpes.exmo.align.impl.edoal.ClassTypeRestriction;
import fr.inrialpes.exmo.align.impl.edoal.ClassValueRestriction;
import fr.inrialpes.exmo.align.impl.edoal.Comparator;
import fr.inrialpes.exmo.align.impl.edoal.Datatype;
import fr.inrialpes.exmo.align.impl.edoal.EDOALVisitor;
import fr.inrialpes.exmo.align.impl.edoal.Expression;
import fr.inrialpes.exmo.align.impl.edoal.InstanceExpression;
import fr.inrialpes.exmo.align.impl.edoal.InstanceId;
import fr.inrialpes.exmo.align.impl.edoal.Linkkey;
import fr.inrialpes.exmo.align.impl.edoal.LinkkeyEquals;
import fr.inrialpes.exmo.align.impl.edoal.LinkkeyIntersects;
import fr.inrialpes.exmo.align.impl.edoal.PathExpression;
import fr.inrialpes.exmo.align.impl.edoal.PropertyConstruction;
import fr.inrialpes.exmo.align.impl.edoal.PropertyDomainRestriction;
import fr.inrialpes.exmo.align.impl.edoal.PropertyExpression;
import fr.inrialpes.exmo.align.impl.edoal.PropertyId;
import fr.inrialpes.exmo.align.impl.edoal.PropertyRestriction;
import fr.inrialpes.exmo.align.impl.edoal.PropertyTypeRestriction;
import fr.inrialpes.exmo.align.impl.edoal.PropertyValueRestriction;
import fr.inrialpes.exmo.align.impl.edoal.RelationCoDomainRestriction;
import fr.inrialpes.exmo.align.impl.edoal.RelationConstruction;
import fr.inrialpes.exmo.align.impl.edoal.RelationDomainRestriction;
import fr.inrialpes.exmo.align.impl.edoal.RelationExpression;
import fr.inrialpes.exmo.align.impl.edoal.RelationId;
import fr.inrialpes.exmo.align.impl.edoal.RelationRestriction;
import fr.inrialpes.exmo.align.impl.edoal.Transformation;
import fr.inrialpes.exmo.align.impl.edoal.Value;
import fr.inrialpes.exmo.align.impl.edoal.ValueExpression;
import fr.inrialpes.exmo.align.parser.SyntaxElement.Constructor;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.semanticweb.owl.align.AlignmentException;
import uk.soton.service.mediation.STriple;

/**
 * The PropertyVisitor class implements the AlignmentVisitor interface and produces MediationResults instances.
 *
 * @author Gianluca Correndo <gc3@ecs.soton.ac.uk>
 *
 */
public class PropertyVisitor implements EDOALVisitor, MediationResultGenerator {

    /**
     * The field mr is the result of the mediation after the alignment visit.
     */
    MediationResult mr;

    /**
     * The field s is the subject node of the produced pattern
     */
    Node s = null;

    /**
     * The field o is the object node of the produced pattern
     */
    Node o = null;

    public PropertyVisitor() {
        this.mr = new MediationResult();
    }

    /**
     * The setS is the setter method for the s field
     *
     * @param s the subject node
     * @return <b>this</b> instance
     *
     */
    public PropertyVisitor setS(Node s) {
        this.s = s;
        return this;
    }

    /**
     * The setO is the setter method for the o field
     *
     * @param o the object node
     * @return <b>this</b> instance
     *
     */
    public PropertyVisitor setO(Node o) {
        this.o = o;
        return this;
    }

    /**
     * The setMediationResult method is the setter for the mr field. It is possible to set an external mediation result,
     * partially composed by other MediationResultGenerator
     *
     * @param mr the MediationResult instance to set
     * @return <b>this</b> instance
     *
     */
    public PropertyVisitor setMediationResult(MediationResult mr) {
        this.mr = mr;
        return this;
    }

    /* (non-Javadoc)
	 * @see org.semanticweb.owl.align.AlignmentVisitor#init(java.util.Properties)
     */
 /*@Override
	public void init(Properties arg0) {
		// NO OP
	}*/
    /**
     * The visit is redirected based on the type of the input object visited.
     *
     * @see org.semanticweb.owl.align.AlignmentVisitor#visit(org.semanticweb.owl.align.Visitable)
     */
    private void innerVisit(Object p) throws AlignmentException {
        if (p instanceof RelationId) {
            this.visit((RelationId) p);
        } else if (p instanceof PropertyId) {
            this.visit((PropertyId) p);
        } else if (p instanceof RelationConstruction) {
            this.visit((RelationConstruction) p);
        } else if (p instanceof PropertyConstruction) {
            this.visit((PropertyConstruction) p);
        } else if (p instanceof PropertyValueRestriction) {
            this.visit((PropertyValueRestriction) p);
        } else {
            Logger.getAnonymousLogger().log(Level.WARNING, "Class not handled yet:" + p.getClass());
        }
        /*TODO: RelationConstruction remains to be handled properly*/
    }

    /* (non-Javadoc)
	 * @see uk.soton.service.mediation.edoal.MediationResultGenerator#getMediationResult()
     */
    @Override
    public MediationResult getMediationResult() {
        return this.mr;
    }

    /**
     * The visit method for RelationId instances
     *
     * @param id RelationId instance
     *
     */
    public void visit(RelationId id) {
        this.addPP(id.getURI().toString());
    }

    /**
     * The visit method for PropertyId instances
     *
     * @param id PropertyId instance
     *
     */
    public void visit(PropertyId id) {
        this.addPP(id.getURI().toString());
    }

    /**
     * The visit method for PropertyValueRestriction instances
     *
     * @param pvr PropertyValueRestriction instance
     * @throws AlignmentException
     *
     */
    public void visit(PropertyValueRestriction pvr) throws AlignmentException {
        if (pvr.getComparator() == Comparator.EQUAL) {
            ValueVisitor vv = ValueVisitor.$();
            vv.visit(pvr.getValue());
            this.mr.getFD().add(vv.getFD());
            Logger.getAnonymousLogger().log(Level.WARNING, "Case not handled yet. v = " + vv.getValue());
        } else {
            Logger.getAnonymousLogger().log(Level.WARNING, "Only EQUAL comparator handled.");
        }
    }

    /**
     * The visit method for RelationConstruction instances
     *
     * @param rc RelationConstruction instance
     * @throws AlignmentException
     *
     */
    public void visit(RelationConstruction rc) throws AlignmentException {
        if (rc.getOperator() == Constructor.COMP) {
            this.patternComposeVisit(rc.getComponents());
        } else if (rc.getOperator() == Constructor.AND) {
            this.patternAndVisit(rc.getComponents());
        }
    }

    /**
     * The visit method for PropertyConstruction instances
     *
     * @param pc PropertyConstruction instance
     * @throws AlignmentException
     *
     */
    public void visit(PropertyConstruction pc) throws AlignmentException {
        if (pc.getOperator() == Constructor.COMP) {
            this.patternComposeVisit(pc.getComponents());
        } else if (pc.getOperator() == Constructor.AND) {
            this.patternAndVisit(pc.getComponents());
        }
    }

    /**
     * The patternAndVisit method visits a collection of PathExpressions (AND, COMP, ..)
     *
     * @param cpe a Collection of PathExpression instances to visit
     * @throws AlignmentException
     *
     */
    private void patternAndVisit(Collection<? extends PathExpression> cpe) throws AlignmentException {
        Node cs = getNode(this.s);
        Node co = getNode(this.o);
        for (PathExpression pe : cpe) {
            PropertyVisitor.$().setS(cs).setO(co).setMediationResult(this.mr).visit(pe);
        }
    }

    /**
     * The patternComposeVisit method visits a collection of PathExpressions (COMP)
     *
     * @param cpe a Collection of PathExpression instances to visit
     * @throws AlignmentException
     *
     */
    private void patternComposeVisit(Collection<? extends PathExpression> cpe) throws AlignmentException {
        Node s = this.s;
        Node o = null;
        for (PathExpression pe : cpe) {
            o = Utility.getNewVar();//Node.createAnon(AnonId.create(getSymb()));
            PropertyVisitor.$().setS(s).setO(o).setMediationResult(mr).visit(pe);
            s = NodeFactory.createBlankNode(o.getBlankNodeId());
        }
        if (this.mr.getPatterns().size() > 0) {
            STriple last = this.mr.getPatterns().get(this.mr.getPatterns().size() - 1);
            last.setO(this.o);
        }
    }

    /**
     * The addPP method add a basic <s,p,o> pattern to the result patterns
     *
     * @param uri the string representation of the property URI
     *
     */
    private void addPP(String uri) {
        Node p = NodeFactory.createURI(uri);
        Node cs = getNode(this.s);
        Node co = getNode(this.o);
        this.mr.getPatterns().add(new STriple(cs, p, co));
    }

    /**
     * The getNode method returns the input Node if provided, a new Var otherwise
     *
     * @param n input Node
     * @return the input Node if provided, a new Var otherwise
     *
     */
    private Node getNode(Node n) {
        return (n == null ? Utility.getNewVar() : n);
    }

    /**
     * The $ method is a constructor shortcut
     *
     * @return a new instance of the class
     *
     */
    public static PropertyVisitor $() {
        return new PropertyVisitor();
    }

    public void visit(PathExpression o) throws AlignmentException {
        innerVisit(o);
    }

    public void visit(Expression o) throws AlignmentException {
        innerVisit(o);
    }

    public void visit(ClassExpression o) throws AlignmentException {
        innerVisit(o);
    }

    @Override
    public void visit(ClassId o) throws AlignmentException {
        innerVisit(o);
    }

    @Override
    public void visit(ClassConstruction o) throws AlignmentException {
        innerVisit(o);
    }

    public void visit(ClassRestriction o) throws AlignmentException {
        innerVisit(o);
    }

    @Override
    public void visit(ClassTypeRestriction o) throws AlignmentException {
        innerVisit(o);
    }

    @Override
    public void visit(ClassDomainRestriction o) throws AlignmentException {
        innerVisit(o);
    }

    @Override
    public void visit(ClassValueRestriction o) throws AlignmentException {
        innerVisit(o);
    }

    @Override
    public void visit(ClassOccurenceRestriction o) throws AlignmentException {
        innerVisit(o);
    }

    public void visit(PropertyExpression o) throws AlignmentException {
        innerVisit(o);
    }

    public void visit(PropertyRestriction o) throws AlignmentException {
        innerVisit(o);
    }

    @Override
    public void visit(PropertyDomainRestriction o) throws AlignmentException {
        innerVisit(o);
    }

    @Override
    public void visit(PropertyTypeRestriction o) throws AlignmentException {
        innerVisit(o);
    }

    public void visit(RelationExpression o) throws AlignmentException {
        innerVisit(o);
    }

    public void visit(RelationRestriction o) throws AlignmentException {
        innerVisit(o);
    }

    @Override
    public void visit(RelationDomainRestriction o) throws AlignmentException {
        innerVisit(o);
    }

    @Override
    public void visit(RelationCoDomainRestriction o) throws AlignmentException {
        innerVisit(o);
    }

    public void visit(InstanceExpression o) throws AlignmentException {
        innerVisit(o);
    }

    @Override
    public void visit(InstanceId o) throws AlignmentException {
        innerVisit(o);
    }

    @Override
    public void visit(Transformation o) throws AlignmentException {
        innerVisit(o);
    }

    public void visit(ValueExpression o) throws AlignmentException {
        innerVisit(o);
    }

    @Override
    public void visit(Value o) throws AlignmentException {
        innerVisit(o);
    }

    @Override
    public void visit(Apply o) throws AlignmentException {
        innerVisit(o);
    }

    @Override
    public void visit(Datatype o) throws AlignmentException {
        innerVisit(o);
    }

    @Override
    public void visit(Aggregate agrgt) throws AlignmentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visit(Linkkey lnk) throws AlignmentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visit(LinkkeyEquals le) throws AlignmentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visit(LinkkeyIntersects li) throws AlignmentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
