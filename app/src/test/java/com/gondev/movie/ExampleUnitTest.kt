package com.gondev.movie

import org.junit.Assert
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    fun solution(maps: Array<String>): IntArray {
        var answer = intArrayOf(0,0)
        val hashMap=LinkedHashMap<Char,MutableList<Char>>()

        for (i in 0 until maps.size) {
            val map=maps[i]
            for (j in 0 until map.length){
                if(map[j]=='.')
                    continue

                // 오른쪽 테스트
                if(j<map.length-1 && map[j]!='.' && map[j+1]!='.' && map[j]!=map[j+1]){
                    // 국경선
                    val key=map[j]
                    val value=map[j+1]
                    if(hashMap[map[j]]==null){
                        hashMap[map[j]]= mutableListOf()
                    }
                    hashMap[map[j]]?.add(map[j+1])

                    if(hashMap[value]==null)
                        hashMap[value]= mutableListOf()
                    hashMap[value]?.add(key)
                }

                // 아래쪽 테스트
                if(i < maps.size-1 && maps[i][j]!='.' && maps[i+1][j]!='.' && maps[i][j]!=maps[i+1][j]){
                    val key=map[j]
                    val value=maps[i+1][j]

                    if(hashMap[map[j]]==null){
                        hashMap[map[j]]= mutableListOf()
                    }
                    hashMap[map[j]]?.add(maps[i+1][j])

                    if(hashMap[value]==null)
                        hashMap[value]= mutableListOf()
                    hashMap[value]?.add(key)
                }
            }
        }

        // 중복 제거
        val newHashMap=LinkedHashMap<Char,MutableList<Char>>()
        hashMap.forEach {
            newHashMap[it.key]=it.value.distinct().toMutableList()
        }

        val pairList = mutableListOf<String>()
        val pairCount=HashMap<Char, Int>()

        newHashMap.forEach { key, list ->
            list.forEachIndexed { index, value ->
                if (!pairList.contains("$value$key") && !pairList.contains("$key$value")) {
                    pairList.add("$key$value")
                }

                if(pairCount[key]==null)
                    pairCount[key]=0
                pairCount[key]= pairCount[key]?.plus(1)!!
            }
        }
        answer[0]=pairList.distinct().size
        answer[1]=pairCount.maxBy {
            it.value
        }?.value?:0

        println(answer)
        return answer
    }

    @Test
    fun addition_isCorrect() {
        //convert(33,4)
        /*Assert.assertArrayEquals(intArrayOf(4,3), solution(arrayOf(
            "..........",
            "AAACC.....",
            ".AAA.....Z",
            "..AAAA..C.",
            "...BBBBB..",
            "....BBB...",
            "...ZBBB...",
            "ZZZZAAAC..",
            ".....CCCC.",
            "QQ......C.",
            "..........")))
        Assert.assertArrayEquals(intArrayOf(0,0), solution(arrayOf(
            "A.B.C.D",
            ".B.C.D.")))*/
        Assert.assertArrayEquals(intArrayOf(0,0), solution(arrayOf(
            "AAAAA",
            "B....")))
        Assert.assertArrayEquals(intArrayOf(9,4), solution(arrayOf(
            "..........",
            "..AAAA....",
            "...BBCC...",
            "...BBDD.Y.",
            "...ZZZZZY.",
            ".QQ...QQQ.",
            "...O......",
            ".SSSS.P...",
            "..SSSS....",
            "WWWWWWWWWW",
            "WWWWWWWWWW")))

        //Assert.assertEquals(28, solution("BCAAAAAAAAANAAAAAAAAAAAAAAAAAAAAB"))
    }
}
