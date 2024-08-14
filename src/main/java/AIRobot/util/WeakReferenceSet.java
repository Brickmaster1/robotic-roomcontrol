/*
Copyright (c) 2024 Aditya Mogli

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list
   of conditions, and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice, this list
   of conditions, and the following disclaimer in the documentation and/or
   other materials provided with the distribution.
3. Neither the name of [Your Name or Your Organization] nor the names of its contributors
   may be used to endorse or promote products derived from this software without specific
   prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS LICENSE.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package AIRobot.util;

import java.util.*;

/**
 * WeakReferenceSet has set behaviour but contains weak references, not strong ones.
 * WeakReferenceSet is thread-safe. It's designed primarily for relatively small sets, as
 * the implementation employed is inefficient on large sets.
 */
public class WeakReferenceSet<E> implements Set<E>
    {
    //----------------------------------------------------------------------------------------------
    // State
    //----------------------------------------------------------------------------------------------

    WeakHashMap<E,Integer> members = new WeakHashMap<E,Integer>();

    //----------------------------------------------------------------------------------------------
    // Primitive Operations
    //----------------------------------------------------------------------------------------------

    @Override
    public boolean add(E o)
        {
        synchronized (members)
            {
            return members.put(o, 1) == null;
            }
        }

    @Override
    public boolean remove(Object o)
        {
        synchronized (members)
            {
            return members.remove(o) != null;
            }
        }

    @Override
    public boolean contains(Object o)
        {
        synchronized (members)
            {
            return members.containsKey(o);
            }
        }

    //----------------------------------------------------------------------------------------------
    // Operations
    //----------------------------------------------------------------------------------------------

    @Override
    public boolean addAll(Collection<? extends E> collection)
        {
        synchronized (members)
            {
            boolean modified = false;
            for (E o : collection)
                {
                if (this.add(o)) modified = true;
                }
            return modified;
            }
        }

    @Override
    public void clear()
        {
        synchronized (members)
            {
            members.clear();
            }
        }

    @Override
    public boolean containsAll(Collection<?> collection)
        {
        synchronized (members)
            {
            for (Object o : collection)
                {
                if (!contains(o)) return false;
                }
            return true;
            }
        }

    @Override
    public boolean isEmpty()
        {
        return this.size()==0;
        }

    @Override
    public int size()
        {
        synchronized (members)
            {
            return members.size();
            }
        }

    @Override
    public Object[] toArray()
        {
        synchronized (members)
            {
            List<Object> list = new LinkedList<>();
            for (Object o : members.keySet())
                {
                list.add(o);
                }
            return list.toArray();
            }
        }

    @Override
    public Iterator<E> iterator()
    // NOTE: copies the set in order to iterate
        {
        synchronized (members)
            {
            List<E> list = new LinkedList<>();
            for (E o : members.keySet())
                {
                list.add(o);
                }
            return list.iterator();
            }
        }

    @Override
    public boolean removeAll(Collection<?> collection)
        {
        synchronized (members)
            {
            boolean modified = false;
            for (Object o : collection)
                {
                if (remove(o)) modified = true;
                }
            return modified;
            }
        }

    @Override
    public boolean retainAll(Collection<?> collection)
        {
        synchronized (members)
            {
            boolean modified = false;
            for (Object o : this)
                {
                if (!collection.contains(o))
                    {
                    if (remove(o)) modified = true;
                    }
                }
            return modified;
            }
        }

    @Override
    public Object[] toArray(Object[] array)
        {
        synchronized (members)
            {
            Object[] cur = this.toArray();
            Object[] result = cur.length > array.length ? (new Object[cur.length]) : array;
            int i = 0;
            for (;i < cur.length; i++)
                {
                result[i] = cur[i];
                }
            for (; i < result.length; i++)
                {
                result[i] = null;
                }
            return result;
            }
        }
    }



























