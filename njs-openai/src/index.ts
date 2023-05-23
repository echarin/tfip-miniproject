import { Configuration, OpenAIApi } from 'openai';
import { config } from 'dotenv';

config(); // load environment variables

async function main() {
    const configuration = new Configuration({
        organization: process.env.ORGANIZATION,
        apiKey: process.env.OPENAI_API_KEY,
    });
    const openai = new OpenAIApi(configuration);
    
    try {
        const completion = await openai.createCompletion({
            model: "text-davinci-003",
            prompt: "Hello world",
        });

        console.log(completion.data.choices[0].text);
    } catch (error) {
        console.error("An error occured: ", error);
    }
}

// main();