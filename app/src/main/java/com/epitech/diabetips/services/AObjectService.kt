package com.epitech.diabetips.services

import com.epitech.diabetips.storages.*

abstract class AObjectService<Object: Any>(baseRoute: String) : AService(baseRoute) {

    inline fun <reified Obj: Object> getAll(page: PaginationObject, name: String = "") : FuelResponse<Array<Obj>> {
        return getRequest((if (name.isBlank()) "?" else "?name=$name&") + page.getRequestParameters())
    }

    inline fun <reified Obj: Object> get(id: Int) : FuelResponse<Obj> {
        return get(id.toString())
    }

    inline fun <reified Obj: Object> get(uid: String = "") : FuelResponse<Obj> {
        return getRequest(if (uid.isBlank()) "" else "/$uid")
    }

    inline fun <reified Obj: Object> remove(id: Int) : FuelResponse<Obj> {
        return remove(id.toString())
    }

    inline fun <reified Obj: Object> remove(uid: String) : FuelResponse<Obj> {
        return deleteRequest(if (uid.isBlank()) "" else "/$uid")
    }

    inline fun <reified Obj: Object> createOrUpdate(obj: Obj, id: Int) : FuelResponse<Obj> {
        return createOrUpdate(obj, id.toString())
    }

    inline fun <reified Obj: Object> createOrUpdate(obj: Obj, uid: String = "") : FuelResponse<Obj> {
        if (uid == "me" || (uid.toIntOrNull() ?: 0) > 0) {
            return update(obj, uid)
        }
        return add(obj)
    }

    inline fun <reified Obj: Object> add(obj: Obj) : FuelResponse<Obj> {
        return postRequest(obj)
    }

    inline fun <reified Obj: Object> update(obj: Obj, id: Int) : FuelResponse<Obj> {
        return update(obj, id.toString())
    }

    inline fun <reified Obj: Object> update(obj: Obj, uid: String = "") : FuelResponse<Obj> {
        return putRequest(obj, if (uid.isBlank()) "" else "/$uid")
    }

}