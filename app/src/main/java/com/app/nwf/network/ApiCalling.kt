package com.app.nwf.network


class ApiCalling private constructor(){

    companion object {

//        fun getAllPlanApi(context: Context){
//            val mToken: RequestBody = Constant.GET_ALL_PLAN.toRequestBody("multipart/form-data".toMediaTypeOrNull())
//
//            WebAPIManager.instance.getAllPlans(mToken).enqueue(object : RemoteCallback<PlanResponse>() {
//                override fun onSuccess(response: PlanResponse?) {
//                    if (response?.status == 1) {
//                        BaseUtils.putAllPlans(context, response.data!!)
//                        callBroadCast(context, Constant.PROGRAM_PLANS, 1, "")
//                    }else{
//                        callBroadCast(context, Constant.PROGRAM_PLANS, 0, "Plans not found...")
//                    }
//                }
//
//                override fun onUnauthorized(throwable: Throwable) {
//                    callBroadCast(context, Constant.PROGRAM_PLANS, 0, "Un-authorized request...")
//                }
//
//                override fun onFailed(throwable: Throwable) {
//                    callBroadCast(context, Constant.PROGRAM_PLANS, 0, "Server Error...")
//                }
//
//                override fun onInternetFailed() {
//                    callBroadCast(context, Constant.PROGRAM_PLANS, 0, "Please check your internet...")
//                }
//
//                override fun onEmptyResponse(message: String) {
//                    callBroadCast(context, Constant.PROGRAM_PLANS, 0, "No response from server...")
//                }
//            })
//        }
//
//        fun getProfile(context: Context){
//            val mToken: RequestBody = Constant.GET_PROFILE.toRequestBody("multipart/form-data".toMediaTypeOrNull())
//            val mUserId: RequestBody = BaseUtils.getUserId(context)!!.toRequestBody("multipart/form-data".toMediaTypeOrNull())
//
//            WebAPIManager.instance.fullProfile(mToken, mUserId).enqueue(object : RemoteCallback<ProfileResponse>() {
//                override fun onSuccess(response: ProfileResponse?) {
//                    if (response?.status == 1) {
//                        BaseUtils.putProfile(context, response.profile!!)
//                        if(response.profile?.membership != null) {
//                            BaseUtils.putMembership(context, response.profile?.membership!!)
//                        }
//                        callBroadCast(context, Constant.FULL_PROFILE, 1, "")
//                    }else{
//                        callBroadCast(context, Constant.FULL_PROFILE, 0, "Profile not found...")
//                    }
//                }
//
//                override fun onUnauthorized(throwable: Throwable) {
//                    callBroadCast(context, Constant.FULL_PROFILE, 0, "Un-authorized request...")
//                }
//
//                override fun onFailed(throwable: Throwable) {
//                    callBroadCast(context, Constant.FULL_PROFILE, 0, "Server Error...")
//                }
//
//                override fun onInternetFailed() {
//                    callBroadCast(context, Constant.FULL_PROFILE, 0, "Please check your internet...")
//                }
//
//                override fun onEmptyResponse(message: String) {
//                    callBroadCast(context, Constant.FULL_PROFILE, 0, "No response from server...")
//                }
//            })
//        }
//
//        fun callBroadCast(context: Context, eventName: String , type: Int, message: String){
//            val intent = Intent(eventName)
//            intent.putExtra("message", message)
//            intent.putExtra("type", type)
//            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
//        }
//
//        fun getWarmUps(context: Context) {
//
//            val mToken: RequestBody = Constant.WARMS_UPS.toRequestBody("multipart/form-data".toMediaTypeOrNull())
//
//            WebAPIManager.instance.getWarmUps(mToken)
//                .enqueue(object : RemoteCallback<WarmupResponse>() {
//                    override fun onSuccess(response: WarmupResponse?) {
//                        if (response?.status == "1") {
//                            BaseUtils.putWarmups(context, response)
//                        }
//                    }
//
//                    override fun onUnauthorized(throwable: Throwable) {}
//
//                    override fun onFailed(throwable: Throwable) {}
//
//                    override fun onInternetFailed() {}
//
//                    override fun onEmptyResponse(message: String) {                    }
//                })
//        }

    }
}