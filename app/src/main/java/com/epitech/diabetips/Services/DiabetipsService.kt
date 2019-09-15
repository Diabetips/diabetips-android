package com.epitech.diabetips.Services

import android.app.Application
import android.util.Log
import com.epitech.diabetips.Managers.AccountManager
import com.epitech.diabetips.Storages.*
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpPut
import com.github.kittinunf.fuel.rx.rx_response
import com.github.kittinunf.fuel.rx.rx_responseObject
import com.github.kittinunf.result.Result
import com.google.gson.GsonBuilder
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

typealias FuelResponse<ResponseObject> = Single<Pair<Response, Result<ResponseObject, FuelError>>>

class DiabetipsService {

    val customGson = GsonBuilder()
        .registerTypeAdapter(AccountObject::class.java, AccountObjectAdapter())
        .create()

    private object Holder { val INSTANCE = DiabetipsService() }

    companion object {
        val instance: DiabetipsService by lazy { Holder.INSTANCE }
    }

    fun login(account: AccountObject) : FuelResponse<AccountObject> {
        return "login".httpPost().body(customGson.toJson(account))
            .rx_responseObject(AccountObject.Deserializer())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.d("login error", err.message)
            }
    }

    fun registerUser(account: AccountObject) : FuelResponse<AccountObject> {
        return "v1/users".httpPost().body(customGson.toJson(account))
            .rx_responseObject(AccountObject.Deserializer())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.d("registerUser error", err.message)
            }
    }

    fun getUser(uid: String) : FuelResponse<AccountObject> {
        return ("v1/users/" + uid).httpGet()
            .rx_responseObject(AccountObject.Deserializer())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.d("getUser error", err.message)
            }
    }

    fun getAllUsers() : FuelResponse<Array<AccountObject>> {
        return ("v1/users/").httpGet()
            .rx_responseObject(AccountObject.ArrayDeserializer())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.d("getAllUsers error", err.message)
            }
    }

    fun updateUser(account: AccountObject) : FuelResponse<AccountObject> {
        return ("v1/users/" + account.uid).httpPut().body(customGson.toJson(account))
            .rx_responseObject(AccountObject.Deserializer())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.d("updateUser error", err.message)
            }
    }

    fun getAllUserMeals(uid: String) : FuelResponse<Array<MealObject>> {
        return ("/" + uid + "/meals/").httpGet()
            .rx_responseObject(MealObject.ArrayDeserializer())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.d("getAllUserMeals error", err.message)
            }
    }

    fun getUserMeal(uid: String, id: String) : FuelResponse<MealObject> {
        return ("/" + uid + "/meals/" + id).httpGet()
            .rx_responseObject(MealObject.Deserializer())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.d("getUserMeal error", err.message)
            }
    }

    fun addUserMeal(uid: String, meal: MealObject) : FuelResponse<MealObject> {
        return ("/" + uid + "/meals/").httpPost().body(customGson.toJson(meal))
            .rx_responseObject(MealObject.Deserializer())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.d("addUserMeal error", err.message)
            }
    }

    fun updateUserMeal(uid: String, meal: MealObject) : FuelResponse<MealObject> {
        return ("/" + uid + "/meals/" + meal.uid).httpPut().body(customGson.toJson(meal))
            .rx_responseObject(MealObject.Deserializer())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.d("updateUserMeal error", err.message)
            }
    }

    fun getAllRecipes() : FuelResponse<Array<RecipeObject>> {
        return ("/").httpGet()
            .rx_responseObject(RecipeObject.ArrayDeserializer())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.d("getAllRecipes error", err.message)
            }
    }

    fun getRecipe(id: String) : FuelResponse<RecipeObject> {
        return ("/" + id).httpGet()
            .rx_responseObject(RecipeObject.Deserializer())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.d("getFood error", err.message)
            }
    }

    fun createRecipe(recipe: RecipeObject) : FuelResponse<RecipeObject> {
        return ("/").httpPost().body(customGson.toJson(recipe))
            .rx_responseObject(RecipeObject.Deserializer())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.d("createRecipe error", err.message)
            }
    }

    fun updateRecipe(recipe: RecipeObject) : FuelResponse<RecipeObject> {
        return ("/" + recipe.uid).httpPut().body(customGson.toJson(recipe))
            .rx_responseObject(RecipeObject.Deserializer())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.d("updateRecipe error", err.message)
            }
    }

    fun getAllFood() : FuelResponse<Array<FoodObject>> {
        return ("/").httpGet()
            .rx_responseObject(FoodObject.ArrayDeserializer())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.d("getAllFood error", err.message)
            }
    }

    fun getFood(id: String) : FuelResponse<FoodObject> {
        return ("/" + id).httpGet()
            .rx_responseObject(FoodObject.Deserializer())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.d("getFood error", err.message)
            }
    }

    fun addFood(food: FoodObject) : FuelResponse<FoodObject> {
        return ("/").httpPost().body(customGson.toJson(food))
            .rx_responseObject(FoodObject.Deserializer())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).doOnError { err ->
                Log.d("addFood error", err.message)
            }
    }

}