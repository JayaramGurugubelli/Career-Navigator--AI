package career_Navigator_parent.ats.engine;


import org.springframework.stereotype.Component;


@Component
public class AtsScoreCalculator {



    public int calculate(
            int matched,
            int total
    ){


        if(total==0)
            return 0;



        return
                (matched * 100)
                        /
                        total;


    }


}