import random
from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI()


class GameContext(BaseModel):
    status: str
    player_total: int
    dealer_total: int
    bet_amount: int


DICTIONARY = {
    "DEALER_WON": [
        "Math is hard, isn't it? Thanks for the {bet_amount} coins.",
        "Thanks for another donation to the casino.",
        "Did you really think {player_total} would beat me?",
        "Nice try."
    ],
    "PLAYER_WON": [
        "Counting cards, huh?",
        "Take your coins. Shut up",
        "You got lucky this time.",
        "Beginner's luck. Try that again."
    ],
    "PLAYER_BLACKJACK": [
        "Piss off.",
        "You just got lucky."
    ],
    "PUSH": [
        "A tie. We both got {dealer_total}. You're wasting my time.",
        "Take your {bet_amount} coins back. Try actually winning next time."
    ]
}


@app.post("/api/dealer/comment")
async def generate_comment(context: GameContext):
    category = DICTIONARY.get(context.status, DICTIONARY["DEALER_WON"])

    raw_quote = random.choice(category)

    formatted_quote = raw_quote.format(
        bet_amount=context.bet_amount,
        player_total=context.player_total,
        dealer_total=context.dealer_total
    )

    return {"comment": formatted_quote}


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=8000)