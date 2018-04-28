package lotrec.dataStructure.expression;

import java.util.Hashtable;
import java.util.Enumeration;
import lotrec.util.Duplicateable;
import lotrec.util.Duplicator;
import lotrec.util.DuplicateException;

/**
Links schemes with their concrete objects (named instances).
Usually, but in construction, an instance set cannot be modified. So, users should use the <i>plus</i> method which creates a completed instance set.
Note : this class uses the <i>hashCode</i> method of the schemes.
@author David Fauthoux
 */
public class InstanceSet implements Duplicateable {

    private Hashtable<SchemeVariable, Object> table;

    /**
    Creates an empty set. Construction can be completed with the <i>put</i> method.
     */
    public InstanceSet() {
        table = new Hashtable<SchemeVariable, Object>();
    }

    /**
    Adds a link between the specified scheme and the specified concrete object.
    Usually, but in construction, an instance set cannot be modified. So, users should use the <i>plus</i> method which creates a completed instance set.
    This method should be used just after the construction.
    @param scheme the scheme which will reference here the concrete object
    @param object the referenced concrete object
     */
    public void put(SchemeVariable scheme, Object instance) {
        table.put(scheme, instance);
    }

    /**
    Returns the referenced concrete object by the specified scheme
    @param scheme the scheme linking the concrete object
    @return the referenced object
     */
    public Object get(SchemeVariable scheme) {
        return table.get(scheme);
    }

    public Hashtable<SchemeVariable, Object> getTable() {
        return table;
    }

    /**
    Returns a completed instance set, completed with a new link between the specified scheme and the specified instance.
    Note : this method does not test whether the scheme is yet referencing another instance, and, in this case, destroy the old link.
    @param scheme the scheme used to link the instance in this set
    @param instance the linked concrete object
    @return a completed instance set
     */
    //attention : ne teste pas si un schema a plusieurs instances
    public InstanceSet plus(SchemeVariable scheme, Object instance) {
        Object i = table.get(scheme);
        if (i == null) {
            InstanceSet s = new InstanceSet(this); //make a copy of the current InstanceSet
            s.put(scheme, instance);
            return s;
        }
        if (i == instance) {
            return this;
        }
        return null;
    }

    @Override
    public String toString() {
        return table.toString();
    }

    private SchemeVariable getFirstKeyOfValue(Object value) {
        for (SchemeVariable each_v : table.keySet()) {
            if (table.get(each_v).equals(value)) {
                return each_v;
            }
        }
        return null;
    }

    public boolean equalsCommutativelyV2(Object ob) { //Do exactly the same as equalsCommutatively()
        if (ob instanceof InstanceSet) {
            InstanceSet o = (InstanceSet) ob;
            for (SchemeVariable each_v_in_this : this.table.keySet()) {
                if (o.table.containsKey(each_v_in_this)) {
                    if (!this.table.get(each_v_in_this).equals(o.table.get(each_v_in_this))) {
                        if ((this.table.contains(o.table.get(each_v_in_this))) && (o.table.contains(this.table.get(each_v_in_this)))) {
                            if (this.getFirstKeyOfValue(o.table.get(each_v_in_this)).equals(
                                    o.getFirstKeyOfValue(this.table.get(each_v_in_this)))) {
                                //Nothing to do..
                                //continue; with the next v_in_this..
                            } else {
                                return false;
                            }
//                            for (SchemeVariable other_v_in_this : this.table.keySet()) {
//                                if (this.table.get(other_v_in_this).equals(o.table.get(each_v_in_this))) {
//                                    for (SchemeVariable other_v_in_o : o.table.keySet()) {
//                                        if (o.table.get(other_v_in_o).equals(this.table.get(each_v_in_this))) {
//                                            if (other_v_in_this.equals(other_v_in_o)) {
//                                                break; // ok for the moment, so break outside this for...
//                                            } else {
//                                                return false;
//                                            }
//                                        } else {
//                                            //Nothing to do..
//                                            //continue; with other_v_in_o
//                                        }
//                                    }
//                                    break; // ok for the moment, so break to continue with other each_v_in_this
//                                } else {
//                                    //Nothing to do..
//                                    //continue; with other_v_in_this
//                                }
//                            }
//                            // the second break is to here..
//                            // since here is nothing, we continue with the next each_v_in_this
                        } else {
                            return false;
                        }
                    } else {
                        //Nothing to do..
                        //continue; with the next v_in_this..
                    }
                } else {
                    return false;
                }
            }
            //When we get here, means all v_in_this were successfully tested...
            return true;
        } else {
            return false;
        }
    }

    public boolean equalsCommutatively(Object o) {
        if (o instanceof InstanceSet) {
            InstanceSet oIS = (InstanceSet) o;
            Hashtable o_table = oIS.getTable();
            for (Enumeration enumr_on_this_table = this.table.keys(); enumr_on_this_table.hasMoreElements();) {
                SchemeVariable v_in_this = (SchemeVariable) enumr_on_this_table.nextElement();
                if (o_table.containsKey(v_in_this)) {
                    Object v_in_this_Instance_In_this = this.table.get(v_in_this);
                    Object v_in_this_Instance_In_o = o_table.get(v_in_this);
                    if (!v_in_this_Instance_In_this.equals(v_in_this_Instance_In_o)) {
                        if ((this.table.contains(v_in_this_Instance_In_o)) && (o_table.contains(v_in_this_Instance_In_this))) {
                            for (Enumeration another_enumr_on_this_table = this.table.keys(); another_enumr_on_this_table.hasMoreElements();) {
                                SchemeVariable another_v_in_this = (SchemeVariable) another_enumr_on_this_table.nextElement();
                                Object another_v_Instance_In_this = this.table.get(another_v_in_this);
                                if (another_v_Instance_In_this.equals(v_in_this_Instance_In_o)) {
                                    for (Enumeration enumr_on_o_table = o_table.keys(); enumr_on_o_table.hasMoreElements();) {
                                        SchemeVariable another_v_in_o = (SchemeVariable) enumr_on_o_table.nextElement();
                                        Object another_v_in_o_Instance_in_o = o_table.get(another_v_in_o);
                                        if (another_v_in_o_Instance_in_o.equals(v_in_this_Instance_In_this)) {
                                            if (!another_v_in_this.equals(another_v_in_o)) {
                                                return false;
                                            }
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                        } else {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            }
            //When we get here, means all v_in_this were successfully tested...
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof InstanceSet) {
            InstanceSet oIS = (InstanceSet) o;
            Hashtable o_table = oIS.getTable();
            return this.table.equals(o_table);
//            for (Enumeration enumr = this.table.keys(); enumr.hasMoreElements();) {
//                SchemeVariable v = (SchemeVariable) enumr.nextElement();
//                if (o_table.containsKey(v)) {
//                    Object v_Instance_In_this = this.table.get(v);
//                    Object v_Instance_In_o = o_table.get(v);
//                    if (!v_Instance_In_this.equals(v_Instance_In_o)) {
//                        return false;
//                    }
//                } else {
//                    return false;
//                }
//            }
//            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.table != null ? this.table.hashCode() : 0);
        return hash;
    }

    //duplication
    /**
    Creates an instance set with the toDuplicate's schemes and instances.
    <b>The instances will be translated to reference the duplicated objects if their images (in <i>translateDuplication</i>), using <code>Duplicator.hasImage</code>.</b>
    @param toDuplicate the instance set to duplicate
     */
    public InstanceSet(InstanceSet toDuplicate) {
        table = (Hashtable) toDuplicate.table.clone();
    }

    public void completeDuplication(Duplicator duplicator) throws ClassCastException {
    }

    public void translateDuplication(Duplicator duplicator) throws DuplicateException {
        for (Enumeration enumr = table.keys(); enumr.hasMoreElements();) {
            SchemeVariable k = (SchemeVariable) enumr.nextElement();
            Object o = table.get(k);
            if (duplicator.hasImage(o)) {
                table.put(k, duplicator.getImage(o));
            }
        }
    }

    public Duplicateable duplicate(Duplicator duplicator) {
        Duplicateable d = new InstanceSet(this);
        duplicator.setImage(this, d);
        return d;
    }
}
